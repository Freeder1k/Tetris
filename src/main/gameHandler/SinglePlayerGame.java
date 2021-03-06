package main.gameHandler;

import main.Block;
import main.BlockQueue;
import main.Tetris;
import main.output.Output;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SinglePlayerGame extends TetrisGame {
    long pauseTime = 0L;

    public SinglePlayerGame(Output output, ScheduledExecutorService timer) {
        super(output, timer, new Color[Tetris.BOARD_HEIGHT][Tetris.BOARD_WIDTH]);
    }

    public void start() {
        for (Color[] row : board) {
            Arrays.fill(row, Color.BLACK);
        }

        blockQueue = new BlockQueue((int) (Math.random() * 10000), board);
        blockQueue.getActive().moveDown();

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);

        updateOutput();
    }

    synchronized void placeBlock() {
        placePlanned = false;
        //Add to board
        Block placed = blockQueue.nextBlock();
        if (placed.isOnTop() || blockQueue.getActive().overlaps()) {
            gameOver();
            return;
        }

        placed.addToBoard();

        blockQueue.getActive().moveDown();

        //Move lines down and add new score.
        for (int i = Tetris.BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean isFull = true;
            for (Color b : board[i]) {
                isFull = isFull && (b != Color.BLACK);
            }
            if (isFull) {
                score += 100;
                for (int i2 = i; i2 < Tetris.BOARD_HEIGHT - 1; i2++) {
                    System.arraycopy(board[i2 + 1], 0, board[i2], 0, Tetris.BOARD_WIDTH);
                }
                Arrays.fill(board[Tetris.BOARD_HEIGHT - 1], Color.BLACK);
            }
        }

        updateOutput();

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
    }

    void gameOver() {
        System.out.println("Game over! Score: " + score);
        nextTimeStep.cancel(true);
        output.setToSingleplayerGameOver(score);
    }

    @Override
    void updateOutput() {
        output.updateSingleplayerOutput(board, blockQueue, score);
    }

    public synchronized void pause() {
        if (nextTimeStep != null) {
            if (!nextTimeStep.isCancelled() && !nextTimeStep.cancel(false)) {
                System.out.println("ERROR: Failed to cancel next time step!");
                return;
            }
        }
        isPaused = true;
        if (!placePlanned)
            pauseTime = System.currentTimeMillis();
    }

    public synchronized void resume() {
        if (isPaused) {
            isPaused = false;
            nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY + pauseTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            pauseTime = 0L;
        }
    }

    public void stop() {
        if (nextTimeStep != null)
            nextTimeStep.cancel(true);
    }
}
