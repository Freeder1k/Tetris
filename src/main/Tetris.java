package main;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Tetris {
    public final static int BOARD_HEIGHT = 22;
    public final static int BOARD_WIDTH = 10;
    private final static int MOVE_DELAY = 1000; //Milliseconds
    private final static int PLACE_DELAY = 500; //Milliseconds
    private final Output output;
    private final Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
    private final BlockQueue blockQueue;
    private final ScheduledExecutorService timer;
    private final InputListener inputListener;
    private int score;
    private ScheduledFuture<?> nextTimeStep;
    private boolean placePlanned = false;
    private boolean isPaused = false;
    private long pauseTime = 0L;

    private Tetris() {
        for (Color[] row : board) {
            Arrays.fill(row, Color.BLACK);
        }

        inputListener = new InputListener(this);
        output = new Output(board, inputListener);
        score = 0;
        blockQueue = new BlockQueue((int) (Math.random() * 1000));
        timer = Executors.newSingleThreadScheduledExecutor();

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
        blockQueue.getActive().moveDown(board);
    }

    public static void main(String[] args) {
        new Tetris();
    }

    private synchronized void runTimedStep() {
        if (!isPaused) {
            //Move active block down.
            if (!blockQueue.getActive().moveDown(board)) {
                placePlanned = true;
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
                return;
            }

            output.updateOutput(blockQueue.getActive(), score);

            nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
        }
    }

    private synchronized void placeBlock() {
        placePlanned = false;
        //Add to board
        Block placed = blockQueue.nextBlock();
        if (placed.isOnTop()) {
            gameOver();
            return;
        }

        placed.addToBoard(board);

        blockQueue.getActive().moveDown(board);

        //Move lines down and add new score.
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean isFull = true;
            for (Color b : board[i]) {
                isFull = isFull && (b != Color.BLACK);
            }
            if (isFull) {
                score += 100;
                for (int i2 = i; i2 < BOARD_HEIGHT - 1; i2++) {
                    System.arraycopy(board[i2 + 1], 0, board[i2], 0, BOARD_WIDTH);
                }
                Arrays.fill(board[BOARD_HEIGHT - 1], Color.BLACK);
            }
        }

        output.updateOutput(blockQueue.getActive(), score);

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
    }

    private void gameOver() {
        System.out.println("Game over! Score: " + score);
        timer.shutdownNow();
        output.removeKeyListener(inputListener);

        //TODO make better
    }

    public synchronized void drop() {
        if (!nextTimeStep.cancel(false)) {
            System.out.println("ERROR: Failed to cancel next time step!");
            return;
        }
        Block block = blockQueue.getActive();
        while (true) {
            if (!block.moveDown(board)) break;
        }
        placeBlock();
    }

    public synchronized void moveLeft() {
        if (!blockQueue.getActive().moveLeft(board))
            return;

        output.updateOutput(blockQueue.getActive(), score);

        if (placePlanned) {
            if (!nextTimeStep.cancel(false)) {
                System.out.println("ERROR: Failed to cancel next time step!");
                return;
            }
            if (blockQueue.getActive().canMoveDown(board)) {
                placePlanned = false;
                nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.SECONDS);
            } else
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
        } else {
            if (!blockQueue.getActive().canMoveDown(board)) {
                if (!nextTimeStep.cancel(false)) {
                    System.out.println("ERROR: Failed to cancel next time step!");
                    return;
                }
                placePlanned = true;
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
            }
        }
    }

    public synchronized void moveRight() {
        if (!blockQueue.getActive().moveRight(board))
            return;

        output.updateOutput(blockQueue.getActive(), score);

        if (placePlanned) {
            if (!nextTimeStep.cancel(false)) {
                System.out.println("ERROR: Failed to cancel next time step!");
                return;
            }
            if (blockQueue.getActive().canMoveDown(board)) {
                placePlanned = false;
                nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.SECONDS);
            } else
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
        } else {
            if (!blockQueue.getActive().canMoveDown(board)) {
                if (!nextTimeStep.cancel(false)) {
                    System.out.println("ERROR: Failed to cancel next time step!");
                    return;
                }
                placePlanned = true;
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
            }
        }
    }

    public synchronized void moveDown() {
        if (!blockQueue.getActive().moveDown(board))
            return;

        output.updateOutput(blockQueue.getActive(), score);

        if (!blockQueue.getActive().canMoveDown(board)) {
            if (!nextTimeStep.cancel(false)) {
                System.out.println("ERROR: Failed to cancel next time step!");
                return;
            }
            placePlanned = true;
            nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void rotate() {
        if (!blockQueue.getActive().rotate(board))
            return;

        output.updateOutput(blockQueue.getActive(), score);

        if (placePlanned) {
            if (!nextTimeStep.cancel(false)) {
                System.out.println("ERROR: Failed to cancel next time step!");
                return;
            }
            if (blockQueue.getActive().canMoveDown(board)) {
                placePlanned = false;
                nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.SECONDS);
            } else
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
        } else {
            if (!blockQueue.getActive().canMoveDown(board)) {
                if (!nextTimeStep.cancel(false)) {
                    System.out.println("ERROR: Failed to cancel next time step!");
                    return;
                }
                placePlanned = true;
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
            }
        }
    }

    public synchronized void pause() {
        if (!nextTimeStep.cancel(false)) {
            System.out.println("ERROR: Failed to cancel next time step!");
            return;
        }
        isPaused = true;
        if(!placePlanned)
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
        timer.shutdownNow();
        output.dispose();
    }
}