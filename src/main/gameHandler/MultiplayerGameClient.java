package main.gameHandler;

import main.Block;
import main.BlockQueue;
import main.Tetris;
import main.multiplayer.TetrisClient;
import main.output.Output;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MultiplayerGameClient extends TetrisGame {
    private final TetrisClient tetrisClient;
    private int receivedLines = 0;

    private MultiplayerGameClient(Output output, ScheduledExecutorService timer, TetrisClient tetrisClient) {
        super(output, timer, new Color[tetrisClient.board_height][tetrisClient.board_width]);
        this.tetrisClient = tetrisClient;
        tetrisClient.setMultiplayerGameClient(this);
        output.setMultiplayerID(tetrisClient.id);
        output.setPlayerCount(tetrisClient.getPlayerCount());
    }

    public static MultiplayerGameClient create(Output output, ScheduledExecutorService timer, String hostName, int port) throws TetrisClient.FailedToCreateException {
        return new MultiplayerGameClient(output, timer, TetrisClient.createClient(hostName, port));
    }


    public void start() {
        for (Color[] row : board) {
            Arrays.fill(row, Color.BLACK);
        }

        blockQueue = new BlockQueue(tetrisClient.seed, board);
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
        if (placed.isOnTop() || blockQueue.getActive().overlaps()) {
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
                    System.arraycopy(board[i2 + 1], 0, board[i2], 0, board[0].length);
                }
                Arrays.fill(board[Tetris.BOARD_HEIGHT - 1], Color.BLACK);
            }
        }
        if (amount > 0) {
            tetrisClient.sendLines(amount);
            receivedLines -= amount;
        }
        if (receivedLines > 0) {
            if (receivedLines > board.length - 2) {
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

        updateOutput();

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
    }

    void gameOver() {
        System.out.println("Game over! Score: " + score);
        nextTimeStep.cancel(true);
        output.setToMultiplayerGameOver(score, tetrisClient.getRank());
        tetrisClient.gameOver();
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
        tetrisClient.shutdown();
        if (nextTimeStep != null)
            nextTimeStep.cancel(true);
    }

    public void stopByHost() {
        if (nextTimeStep != null)
            nextTimeStep.cancel(true);
        output.setToMultiplayerJoin();
    }

    public synchronized void receiveLines(int amount) {
        receivedLines += amount;
    }

    public void setPlayerCount(int amount) {
        output.setPlayerCount(amount);
    }

    public void end(int winnerID) {
        output.setToMultiplayerClientWait(winnerID, tetrisClient.getRank());
    }
}
