package main.gameHandler;

import main.Block;
import main.BlockQueue;
import main.output.Output;
import main.Tetris;
import main.multiplayer.TetrisClient;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MultiplayerGameClient extends TetrisGame {
    private final TetrisClient multiplayerClient;
    private int receivedLines = 0;

    private MultiplayerGameClient(Tetris tetris, Output output, ScheduledExecutorService timer, TetrisClient multiplayerClient) {
        super(output, timer, new Color[multiplayerClient.board_height][multiplayerClient.board_width]);
        this.multiplayerClient = multiplayerClient;

        if (multiplayerClient.board_height != Tetris.BOARD_HEIGHT || multiplayerClient.board_width != Tetris.BOARD_WIDTH) {
            //TODO set output size
        }

        //TODO update output (multiplayerClient.id, playercount, isrunning)
    }

    public static MultiplayerGameClient create(Tetris tetris, Output output, ScheduledExecutorService timer, String hostName, int port) throws TetrisClient.FailedToCreateException {
        return new MultiplayerGameClient(tetris, output, timer, TetrisClient.createClient(tetris, hostName, port));
    }


    public void start() {
        for (Color[] row : board) {
            Arrays.fill(row, Color.BLACK);
        }

        blockQueue = new BlockQueue(multiplayerClient.seed);
        blockQueue.getActive().moveDown(board);

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);

        output.startMultiplayerGame();
        output.updateOutput(blockQueue, score);
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
            multiplayerClient.sendLines(amount);

        output.updateOutput(blockQueue, score);

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
    }

    void gameOver() {
        System.out.println("Game over! Score: " + score);
        multiplayerClient.gameOver();
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
        multiplayerClient.shutdown();
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
