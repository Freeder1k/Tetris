package main.multiplayer;

import main.Tetris;
import main.gameHandler.MultiplayerGameClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class TetrisClient {
    public final int board_width;
    public final int board_height;
    private final Socket tetrisSocket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final AtomicBoolean running;
    private final CompletableFuture<?> serverListener;
    private int playerCount;
    public final int id;
    public int seed;
    public MultiplayerGameClient multiplayerGameClient = null;

    private TetrisClient(String hostName, int port) throws IOException, FailedToCreateException {
        try {
            tetrisSocket = new Socket(hostName, port);
        } catch (UnknownHostException e) {
            throw new FailedToCreateException("Unknown host: " + hostName);
        }

        out = new PrintWriter(tetrisSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(tetrisSocket.getInputStream()));

        String firstLine = in.readLine();
        String[] args = firstLine.split(" ");
        if (args.length != 6 || !args[0].equals("hello:")) {
            tetrisSocket.close();
            out.close();
            in.close();
            throw new FailedToCreateException("Invalid hello message: " + firstLine);
        } else {
            try {
                board_width = Integer.parseInt(args[1]);
                board_height = Integer.parseInt(args[2]);
                playerCount = Integer.parseInt(args[3]);
                id = Integer.parseInt(args[4]);
                running = new AtomicBoolean(Boolean.parseBoolean(args[5]));
            } catch (NumberFormatException ignored) {
                throw new FailedToCreateException("Invalid hello message: " + firstLine);
            }
        }


        serverListener = CompletableFuture.runAsync(() -> {
            String inputLine;
            try {
                inputLine = in.readLine();
            } catch (SocketException ignored) {
                return;
            } catch (IOException e) {
                e.printStackTrace(); //TODO do something to notify user
                try {
                    tetrisSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                out.close();
                try {
                    in.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return;
            }
            boolean stop = false;
            while (inputLine != null) {
                System.out.println(inputLine);
                String[] input = inputLine.split(" ");
                if (input.length == 0) {
                    try {
                        inputLine = in.readLine();
                    } catch (SocketException ignored) {
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    continue;
                }

                switch (input[0]) {
                    case "start":
                        if (input.length == 2) {
                            try {
                                seed = Integer.parseInt(input[1]);
                                multiplayerGameClient.start();
                            } catch (NumberFormatException ignored) {
                                System.out.println("Invalid server input: " + inputLine);
                            }
                        }
                        if(multiplayerGameClient != null)
                            multiplayerGameClient.start();
                        running.set(true);
                        break;
                    case "ended":
                        if(multiplayerGameClient != null)
                            multiplayerGameClient.end();
                        running.set(false);
                        break;
                    case "stopped":
                        stop = true;
                        break;
                    case "sentlines":
                        if (running.get()) {
                            if (input.length == 2) {
                                try {
                                    int amount = Integer.parseInt(input[1]);
                                    //TODO do
                                } catch (NumberFormatException ignored) {
                                    System.out.println("Invalid server input: " + inputLine);
                                }
                            }
                        }
                        break;
                    case "playercount":
                        if (input.length == 2) {
                            try {
                                int amount = Integer.parseInt(input[1]);
                                playerCount = amount;
                                //TODO tetris.setWaitingPlayers(amount);
                            } catch (NumberFormatException ignored) {
                                System.out.println("Invalid server input: " + inputLine);
                            }
                        }
                        break;
                }

                if (stop)
                    break;//TODO close stuff

                try {
                    inputLine = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            try {
                tetrisSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static TetrisClient createClient(String hostName, int port) throws FailedToCreateException {
        try {
            return new TetrisClient(hostName, port);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FailedToCreateException("Failed to create client!");
        }
    }

    public void sendLines(int amount) {
        out.println("sentlines " + amount);
    }

    public void gameOver() {
        out.println("gameover");
        running.set(false);
    }

    public void shutdown() {
        out.println("stopped");

        serverListener.cancel(true);

        try {
            tetrisSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public boolean isRunning() {
        return running.get();
    }

    public void setMultiplayerGameClient(MultiplayerGameClient multiplayerGameClient) {
        this.multiplayerGameClient = multiplayerGameClient;
    }

    public static class FailedToCreateException extends Exception {
        public FailedToCreateException(String message) {
            super(message);
        }
    }
}