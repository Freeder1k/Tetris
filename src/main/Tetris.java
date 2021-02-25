package main;

import java.util.Arrays;
import java.util.concurrent.*;

public class Tetris {
    public final static int BOARD_HEIGHT = 22;
    public final static int BOARD_WIDTH = 10;
    private final Output output;
    private final boolean[][] board = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
    private int score;
    private final BlockQueue blockQueue;
    private final ScheduledExecutorService timer;
    private final InputListener inputListener;
    private ScheduledFuture<?> nextTimeStep;
    private boolean isPaused = false;

    private Tetris() {
        for (boolean[] row : board) {
            Arrays.fill(row, false);
        }

        inputListener = new InputListener(this);
        output = new Output(board, inputListener);
        score = 0;
        blockQueue = new BlockQueue((int) (Math.random() * 1000));
        timer = Executors.newSingleThreadScheduledExecutor();

        nextTimeStep = timer.schedule(this::runTimedStep, 1, TimeUnit.SECONDS);
        blockQueue.getActive().moveDown(board);
    }

    public static void main(String[] args) {
        new Tetris();
    }

    private synchronized void runTimedStep() {
        if(!isPaused) {
            //Move active block down.
            if (!blockQueue.getActive().moveDown(board)) {
                placeBlock();
                return;
            }

            output.updateOutput(blockQueue.getActive(), score);

            nextTimeStep = timer.schedule(this::runTimedStep, 1, TimeUnit.SECONDS);
        }
    }

    private synchronized void placeBlock() {
        long start = System.currentTimeMillis();
        //Add to board
        Block placed = blockQueue.nextBlock();
        if(placed.isOnTop()) {
            gameOver();
            return;
        }

        placed.addToBoard(board);

        blockQueue.getActive().moveDown(board);

        //Move lines down and add new score.
        for (int i = BOARD_HEIGHT-1; i >= 0; i--) {
            boolean isFull = true;
            for (boolean b : board[i]) {
                isFull = isFull && b;
            }
            if(isFull) {
                score += 100;
                for (int i2 = i; i2 < BOARD_HEIGHT-1; i2++) {
                    board[i2] = board[i2 + 1];
                }
                Arrays.fill(board[BOARD_HEIGHT-1], false);
            }
        }

        output.updateOutput(blockQueue.getActive(), score);

        nextTimeStep = timer.schedule(this::runTimedStep, 1, TimeUnit.SECONDS);

        System.out.println("Delay: " + (System.currentTimeMillis() - start) + "ms");
    }

    private void gameOver() {
        System.out.println("Game over! Score: " + score);
        timer.shutdownNow();
        output.removeKeyListener(inputListener);

        //TODO make better
    }

    public synchronized void drop() {
        if(!nextTimeStep.cancel(false)) {
            try {
                nextTimeStep.get();
            } catch (Exception ignored) {}
            if(!nextTimeStep.cancel(true)) {
                System.out.println("ERROR 123");
            }
        }
        Block block = blockQueue.getActive();
        while (true){
            if (!block.moveDown(board)) break;
        }
        placeBlock();
    }

    public synchronized void moveLeft() {
        //TODO stop nextimestep if at bottom
        blockQueue.getActive().moveLeft(board);
        output.updateOutput(blockQueue.getActive(), score);
    }

    public synchronized void moveRight() {
        //TODO stop nextimestep if at bottom
        blockQueue.getActive().moveRight(board);
        output.updateOutput(blockQueue.getActive(), score);
    }

    public synchronized void moveDown() {
        if(!blockQueue.getActive().moveDown(board)) {
            if(!nextTimeStep.cancel(false)) {
                try {
                    nextTimeStep.get();
                } catch (Exception ignored) {}
                if(!nextTimeStep.cancel(true)) {
                    System.out.println("ERROR 123");
                }
            }
            placeBlock();
        }
        else
            output.updateOutput(blockQueue.getActive(), score);
    }

    public synchronized void rotate() {
        //TODO stop nextimestep if at bottom
        blockQueue.getActive().rotate(board);
        output.updateOutput(blockQueue.getActive(), score);
    }

    public synchronized void pause() {
        isPaused = true;
    }

    public synchronized void resume() {
        if(isPaused) {
            isPaused = false;
            runTimedStep();
        }
    }

    public void stop() {
        timer.shutdownNow();
        output.dispose();
    }
}