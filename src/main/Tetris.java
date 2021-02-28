package main;

import main.multiplayer.TetrisClient;
import main.multiplayer.TetrisServer;
import main.output.Output;

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
    private final ScheduledExecutorService timer;
    private BlockQueue blockQueue;
    private int score;
    private ScheduledFuture<?> nextTimeStep;
    private boolean placePlanned = false;
    private boolean isPaused = false;
    private long pauseTime = 0L;
    private boolean isMultiplayer = false;
    private TetrisClient multiplayerClient;
    private int playerCount;
    private boolean customBoard;
    private int[] customBoardSize = new int[2];
    private boolean isMultiplayerRunning;
    private int multiplayerID;
    private boolean isMultiplayerHost;
    private TetrisServer tetrisServer;

    private Tetris() {
        InputListener inputListener = new InputListener(this);
        output = new Output(board, inputListener, this);
        timer = Executors.newSingleThreadScheduledExecutor();
    }

    public static void main(String[] args) {
        new Tetris();
    }

    public void start() {
        for (Color[] row : board) {
            Arrays.fill(row, Color.BLACK);
        }
        score = 0;
        blockQueue = new BlockQueue((int) (Math.random() * 1000));

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
        blockQueue.getActive().moveDown(board);
        output.updateOutput(blockQueue, score);
    }

    private synchronized void runTimedStep() {
        if (!isPaused) {
            //Move active block down.
            if (!blockQueue.getActive().moveDown(board)) {
                placePlanned = true;
                nextTimeStep = timer.schedule(this::placeBlock, PLACE_DELAY, TimeUnit.MILLISECONDS);
                return;
            }

            output.updateOutput(blockQueue, score);

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

        output.updateOutput(blockQueue, score);

        nextTimeStep = timer.schedule(this::runTimedStep, MOVE_DELAY, TimeUnit.MILLISECONDS);
    }

    private void gameOver() {
        System.out.println("Game over! Score: " + score);
        nextTimeStep.cancel(true);
        output.setToGameOverMenu(score);
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

        output.updateOutput(blockQueue, score);

        checkMove();
    }

    public synchronized void moveRight() {
        if (!blockQueue.getActive().moveRight(board))
            return;

        output.updateOutput(blockQueue, score);

        checkMove();
    }

    public synchronized void moveDown() {
        if (!blockQueue.getActive().moveDown(board))
            return;

        output.updateOutput(blockQueue, score);

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

        output.updateOutput(blockQueue, score);

        checkMove();
    }

    private void checkMove() {
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
        if(nextTimeStep != null) {
            if (!nextTimeStep.cancel(false)) {
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
        if(isMultiplayer)
            multiplayerClient.shutdown();
        timer.shutdownNow();
        output.dispose();
    }

    public void joinMultiplayer(String hostName, int port) {
        isMultiplayer = true;
        try {
            multiplayerClient = TetrisClient.createClient(this, hostName, port);
        } catch (TetrisClient.FailedToCreateException e) {
            e.printStackTrace();
            //TODO
            isMultiplayer = false;
            return;
        }

        playerCount = multiplayerClient.getPlayerCount();
        customBoard = multiplayerClient.board_height != BOARD_HEIGHT && multiplayerClient.board_width != BOARD_WIDTH;
        if(customBoard) {
            customBoardSize[0] = multiplayerClient.board_height;
            customBoardSize[1] = multiplayerClient.board_width;
        }

        multiplayerID = multiplayerClient.id;

        isMultiplayerRunning = multiplayerClient.isRunning();
        //TODO update output
    }

    public void leaveMultiplayer() {
        isMultiplayer = false;
        multiplayerClient.shutdown();
    }

    public void hostMultiplayer() {
        isMultiplayer = true;
        isMultiplayerHost = true;
        tetrisServer = TetrisServer.createServer(this);
        if(tetrisServer == null) {
            //TODO
            isMultiplayer = false;
            isMultiplayerHost = false;
        }

        playerCount = 1;

        multiplayerID = 0;
        //TODO update output
    }

    public synchronized void setWaitingPlayers(int amount) {
        //TODO
        playerCount = amount;
    }



    public void startSingleplayerGame() {
        start();
        //TODO
    }
}