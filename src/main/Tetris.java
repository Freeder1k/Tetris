package main;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Tetris {
    public final static int BOARD_HEIGHT = 20;
    public final static int BOARD_WIDTH = 10;
    private final InputListener inputListener;
    private final Output output;
    private final boolean[][] board = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
    private int score;
    private final BlockQueue blockQueue;
    private final ScheduledExecutorService timer;
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
    }

    public static void main(String[] args) {
        new Tetris();
    }

    private synchronized void runTimedStep() {
        if(!isPaused) {
            //Move active block down.
            if (!blockQueue.getActive().moveDown(board)) {
                timer.schedule(this::placeBlock, 500, TimeUnit.MILLISECONDS);
                return;
            }

            output.updateOutput(blockQueue.getActive(), score);

            nextTimeStep = timer.schedule(this::runTimedStep, 1, TimeUnit.SECONDS);
        }
    }

    private synchronized void placeBlock() {
        //Add to board
        blockQueue.nextBlock().addToBoard(board);


        boolean[] fullLines = getFullLines();

        //Move lines down and add new score.
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            if (fullLines[i]) {
                score += 100;
                for (int i2 = i; i2 > 0; i2--) {
                    board[i2] = board[i2 - 1];
                }
                Arrays.fill(board[0], false);
            }
        }

        output.updateOutput(blockQueue.getActive(), score);

        nextTimeStep = timer.schedule(this::runTimedStep, 1, TimeUnit.SECONDS);
    }

    private boolean[] getFullLines() {
        boolean[] lines = new boolean[Tetris.BOARD_HEIGHT];
        Arrays.fill(lines, true);

        for (int i = 0; i < lines.length; i++) {
            for (boolean b : board[i]) {
                lines[i] = lines[i] && b;
            }
        }
        return lines;
    }

    public synchronized void pause() {
        isPaused = true;
    }

    public synchronized void resume() {
        isPaused = false;
        runTimedStep();
    }

    public void stop() {
        timer.shutdownNow();
    }
}