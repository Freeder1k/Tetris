package main.gameHandler;

import main.Block;
import main.BlockQueue;
import main.output.Output;
import main.Tetris;
import main.multiplayer.TetrisServer;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MultiplayerGameHost extends TetrisGame {
    private final TetrisServer tetrisServer;
    private int receivedLines = 0;

    private MultiplayerGameHost(Output output, ScheduledExecutorService timer, TetrisServer tetrisServer) {
        super(output, timer, new Color[Tetris.BOARD_HEIGHT][Tetris.BOARD_WIDTH]);
        this.tetrisServer = tetrisServer;
    }

    public static MultiplayerGameHost create(Tetris tetris, Output output, ScheduledExecutorService timer) {
        TetrisServer host = TetrisServer.createServer(tetris);
        if (host == null)
            return null;

        return new MultiplayerGameHost(output, timer, host);
    }


    public void start() {
        for (Color[] row : board) {
            Arrays.fill(row, Color.BLACK);
        }

        int seed = (int) (Math.random() * 10000);

        tetrisServer.startGame(seed);

        blockQueue = new BlockQueue(seed);
        blockQueue.getActive().moveDown(board);

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);

        output.startMultiplayerGame(board);
        output.updateOutput(board, blockQueue, score);//TODO multiplayer out
    }

    synchronized void placeBlock() {
        //TODO add lines
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
        int amount = 0;
        for (int i = Tetris.BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean isFull = true;
            for (Color b : board[i]) {
                isFull = isFull && (b != Color.BLACK);
            }
            if (isFull) {
                amount++;
                score += 100;
                for (int i2 = i; i2 < Tetris.BOARD_HEIGHT - 1; i2++) {
                    System.arraycopy(board[i2 + 1], 0, board[i2], 0, Tetris.BOARD_WIDTH);
                }
                Arrays.fill(board[Tetris.BOARD_HEIGHT - 1], Color.BLACK);
            }
        }
        if (amount > 0)
            tetrisServer.distributeLines(0, amount);

        output.updateOutput(board, blockQueue, score);

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
    }

    void gameOver() {
        System.out.println("Game over! Score: " + score);
        tetrisServer.removeActivePlayer(0);
        nextTimeStep.cancel(true);
        output.setToMultiplayerGameOverMenu();
    }

    public synchronized void pause() {
        //do nothing
    }

    public synchronized void resume() {
        //do nothing
    }

    public void stop() {
        tetrisServer.shutdown();
        if(nextTimeStep != null)
            nextTimeStep.cancel(true);
    }

    public synchronized void receiveLines(int amount) {
        receivedLines += amount;
        //TODO update output
    }

    public void setPlayerCount(int amount) {
        //TODO update output
    }
}
