package main.multiplayer;

import main.Tetris;

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TetrisServerThread extends Thread{
    public final int id;
    private final int playerCount;
    private final Socket socket;
    private PrintWriter out;
    private final TetrisServer tetrisServer;
    private final AtomicBoolean running;

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
                                socket.getInputStream()));
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);
            String inputLine;
            out.println("hello: " +  Tetris.BOARD_WIDTH + " " + Tetris.BOARD_HEIGHT + " " + playerCount + " " + id + " " + running.get());

            boolean stop = false;
            inputLine = in.readLine();
            while (inputLine!= null) {
                String[] input = inputLine.split(" ");
                if(input.length == 0) {
                    inputLine = in.readLine();
                    continue;
                }

                switch(input[0]) {
                    case "sentlines":
                        if(running.get()) {
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
                        if(running.get())
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame(int seed) {
        out.println("start " + seed);
        running.set(true);
    }

    public void endGame(int winnerID) {
        out.println("ended " + winnerID);
    }

    public void shutdown() {
        out.println("stopped");
        out.close();
        this.interrupt();
    }

    public void sendLines(int amount) {
        out.println("sentlines: " + amount);
    }

    public void setPlayerCount(int playerCount) {
        out.println("playercount: " + playerCount);
    }
}
