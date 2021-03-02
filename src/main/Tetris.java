package main;

import main.gameHandler.MultiplayerGameClient;
import main.gameHandler.MultiplayerGameHost;
import main.gameHandler.SinglePlayerGame;
import main.multiplayer.TetrisClient;
import main.output.Output;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Tetris {
    public final static int BOARD_HEIGHT = 22;
    public final static int BOARD_WIDTH = 10;
    private final Output output;
    private final ScheduledExecutorService timer;
    private final InputListener inputListener;
    private SinglePlayerGame singlePlayerGame;
    private MultiplayerGameHost multiplayerGameHost;
    private MultiplayerGameClient multiplayerGameClient;

    private Tetris() {
        inputListener = new InputListener(this);
        output = new Output(inputListener, this);
        timer = Executors.newSingleThreadScheduledExecutor();
    }

    public static void main(String[] args) {
        new Tetris();
    }

    public void startSingleplayerGame() {
        if (singlePlayerGame == null) {
            singlePlayerGame = new SinglePlayerGame(output, timer);
        }
        inputListener.setGameHandler(singlePlayerGame);

        singlePlayerGame.start();
    }

    public void startMultiplayerGameAsHost() {
        multiplayerGameHost.start();
    }

    public void startMultiplayerClientGame() {
        multiplayerGameClient.start();
    }

    public boolean hostMultiplayerGame() {
        multiplayerGameHost = MultiplayerGameHost.create(this, output, timer);
        if (multiplayerGameHost == null)
            return false;//TODO

        output.setMultiplayerInfo(multiplayerGameHost.getHostName(), multiplayerGameHost.getPort());

        inputListener.setGameHandler(multiplayerGameHost);
        return true;
    }

    public boolean joinMultiplayerGame(String hostName, int port) {
        try {
            multiplayerGameClient = MultiplayerGameClient.create(output, timer, hostName, port);
        } catch (TetrisClient.FailedToCreateException e) {
            //TODO
            System.out.println(e.getMessage());
            return false;
        }

        output.setMultiplayerInfo(hostName, port);

        inputListener.setGameHandler(multiplayerGameClient);
        return true;
    }

    public void leaveMultiplayerGame() {
        if (multiplayerGameHost != null) {
            multiplayerGameHost.stop();
            multiplayerGameHost = null;
        }
        if (multiplayerGameClient != null) {
            multiplayerGameClient.stop();
            multiplayerGameClient = null;
        }
    }

    public void stop() {
        if (multiplayerGameHost != null)
            multiplayerGameHost.stop();

        if (multiplayerGameClient != null)
            multiplayerGameClient.stop();

        if (singlePlayerGame != null)
            singlePlayerGame.stop();

        timer.shutdownNow();
        output.dispose();
    }
}