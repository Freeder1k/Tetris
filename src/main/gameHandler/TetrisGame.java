package main.gameHandler;

import main.Block;
import main.BlockQueue;
import main.output.Output;

import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class TetrisGame {
    final static int MOVE_DELAY = 1000; //Milliseconds
    final static int PLACE_DELAY = 500; //Milliseconds
    final Output output;
    final ScheduledExecutorService timer;
    ScheduledFuture<?> nextTimeStep;
    BlockQueue blockQueue;
    final Color[][] board;
    int score = 0;
    boolean isPaused = false;
    boolean placePlanned = false;

    public TetrisGame(Output output, ScheduledExecutorService timer, Color[][] board) {
        this.output = output;
        this.timer = timer;
        this.board = board;
    }

    public abstract void start();

    synchronized void runTimedStep() {
        if (!isPaused) {
            //Move active block down.
            if (!blockQueue.getActive().moveDown()) {
                placePlanned = true;
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
                return;
            }

            output.updateOutput(board, blockQueue, score);

            nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
        }
    }

    abstract void placeBlock();

    abstract void gameOver();

    public synchronized void drop() {
        if (!nextTimeStep.cancel(false)) {
            System.out.println("ERROR: Failed to cancel next time step!");
            return;
        }
        Block block = blockQueue.getActive();
        while (true) {
            if (!block.moveDown()) break;
        }
        placeBlock();
    }

    public synchronized void moveLeft() {
        if (!blockQueue.getActive().moveLeft())
            return;

        output.updateOutput(board, blockQueue, score);

        checkMove();
    }

    public synchronized void moveRight() {
        if (!blockQueue.getActive().moveRight())
            return;

        output.updateOutput(board, blockQueue, score);

        checkMove();
    }

    public synchronized void moveDown() {
        if (!blockQueue.getActive().moveDown())
            return;

        output.updateOutput(board, blockQueue, score);

        if (!blockQueue.getActive().canMoveDown()) {
            if (!nextTimeStep.cancel(false)) {
                System.out.println("ERROR: Failed to cancel next time step!");
                return;
            }
            placePlanned = true;
            nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void rotate() {
        if (!blockQueue.getActive().rotate())
            return;

        output.updateOutput(board, blockQueue, score);

        checkMove();
    }

    void checkMove() {
        if (placePlanned) {
            if (!nextTimeStep.cancel(false)) {
                System.out.println("ERROR: Failed to cancel next time step!");
                return;
            }
            if (blockQueue.getActive().canMoveDown()) {
                placePlanned = false;
                nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.SECONDS);
            } else
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
        } else {
            if (!blockQueue.getActive().canMoveDown()) {
                if (!nextTimeStep.cancel(false)) {
                    System.out.println("ERROR: Failed to cancel next time step!");
                    return;
                }
                placePlanned = true;
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
            }
        }
    }

    public abstract void pause();

    public abstract void resume();

    public abstract void stop();
}
