package main.gameHandler;

import main.Block;
import main.BlockQueue;
import main.Tetris;
import main.multiplayer.TetrisServer;
import main.output.Output;

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
        tetrisServer.setMultiplayerGameHost(this);
        output.setMultiplayerID(0);
        output.setPlayerCount(1);
    }

    public static MultiplayerGameHost create(Output output, ScheduledExecutorService timer) {
        TetrisServer host = TetrisServer.createServer();
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

        blockQueue = new BlockQueue(seed, board);
        blockQueue.getActive().moveDown();

        receivedLines = 0;

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);

        output.setToMultiplayerInGame(board);
        updateOutput();
    }

    synchronized void placeBlock() {
        placePlanned = false;
        //Add to board
        Block placed = blockQueue.nextBlock();
        if (placed.isOnTop()) {
            gameOver();
            return;
        }

        placed.addToBoard();

        blockQueue.getActive().moveDown();

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
        if (amount > 0) {
            tetrisServer.distributeLines(0, amount);
            receivedLines -= amount;
        }
        if (receivedLines > 0) {
            if(receivedLines > board.length - 2) {
                gameOver();
                return;
            }
            for (int i = board.length - receivedLines - 1; i >= 0; i--) {
                System.arraycopy(board[i], 0, board[i + receivedLines], 0, board[0].length);
            }
            int gap = (int) (Math.random() * board[0].length);
            for (int i = 0; i < receivedLines; i++) {
                Arrays.fill(board[i], Color.LIGHT_GRAY);
                board[i][gap] = Color.BLACK;
            }
        }
        receivedLines = 0;

        if(blockQueue.getActive().overlaps()) {
            gameOver();
            return;
        }

        updateOutput();

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
    }

    void gameOver() {
        System.out.println("Game over! Score: " + score);
        nextTimeStep.cancel(true);
        tetrisServer.removeActivePlayer(0);
        int rank = tetrisServer.getRank();
        if(rank != 1)
            output.setToMultiplayerGameOver(score, rank);
    }

    @Override
    void updateOutput() {
        output.updateMultiplayerOutput(board, blockQueue, score);
    }

    public synchronized void pause() {
        //do nothing
    }

    public synchronized void resume() {
        //do nothing
    }

    public void stop() {
        tetrisServer.shutdown();
        if (nextTimeStep != null)
            nextTimeStep.cancel(true);
    }

    public synchronized void receiveLines(int amount) {
        receivedLines += amount;
    }

    public void setPlayerCount(int amount) {
        output.setPlayerCount(amount);
    }

    public void end(int winnerID) {
        output.setToMultiplayerHostWait(winnerID, tetrisServer.getRank());
    }

    public String getHostName() {
        return tetrisServer.getHostName();
    }

    public int getPort() {
        return tetrisServer.getPort();
    }
}
