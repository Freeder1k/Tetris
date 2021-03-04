package main.multiplayer;

import main.Tetris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class TetrisServerThread extends Thread {
    public final int id;
    private final int playerCount;
    private final Socket socket;
    private final TetrisServer tetrisServer;
    private final AtomicBoolean running;
    private PrintWriter out;

    public TetrisServerThread(Socket socket, int id, int playerCount, TetrisServer tetrisServer, boolean running) {
        super("TetrisServerThread");
        this.socket = socket;
        this.playerCount = playerCount;
        this.id = id;
        this.tetrisServer = tetrisServer;
        this.running = new AtomicBoolean(running);
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()))
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);
            String inputLine;
            out.println("hello: " + Tetris.BOARD_WIDTH + " " + Tetris.BOARD_HEIGHT + " " + playerCount + " " + id + " " + running.get());

            boolean stop = false;
            inputLine = in.readLine();
            System.out.println(inputLine);
            while (inputLine != null) {
                String[] input = inputLine.split(" ");
                if (input.length == 0) {
                    inputLine = in.readLine();
                    continue;
                }

                switch (input[0]) {
                    case "sentlines":
                        if (running.get()) {
                            if (input.length == 2) {
                                try {
                                    tetrisServer.distributeLines(id, Integer.parseInt(input[1]));
                                } catch (NumberFormatException ignored) {
                                    System.out.println("Client ID " + id + " sent invalid input: " + inputLine);
                                }
                            }
                        }
                        break;
                    case "gameover":
                        if (running.get())
                            tetrisServer.removeActivePlayer(id);
                        break;
                    case "stopped":
                        stop = true;
                        break;
                }

                if (stop)
                    break;

                inputLine = in.readLine();
            }
            socket.close();
            out.close();
            tetrisServer.removeServerThread(id);
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame(int seed) {
        out.println("start " + seed);
        out.println("test");
        running.set(true);
    }

    public void endGame(int winnerID) {
        out.println("ended " + winnerID);
        running.set(false);
    }

    public void shutdown() {
        out.println("stopped");
        out.close();
        this.interrupt();
    }

    public void playerDied() {
        out.println("playerdied");
    }

    public void sendLines(int amount) {
        out.println("sentlines " + amount);
    }

    public void setPlayerCount(int playerCount) {
        out.println("playercount " + playerCount);
    }
}
