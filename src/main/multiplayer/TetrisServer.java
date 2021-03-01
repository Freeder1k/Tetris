package main.multiplayer;

import main.Tetris;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TetrisServer {
    private final Tetris tetris;
    private final ServerSocket serverSocket;
    private final CompletableFuture<?> clientListener;
    private final Map<Integer, TetrisServerThread> serverThreads = new ConcurrentHashMap<>();
    private final LinkedList<Integer> activePlayers = new LinkedList<>();
    private final Random gen = new Random((int) (Math.random() * 1000));
    private final AtomicBoolean running = new AtomicBoolean(false);
    private int nextID = 1; //ID 0 = host
    private int playerCount = 1;

    private TetrisServer(Tetris tetris) throws IOException {
        this.tetris = tetris;

        serverSocket = new ServerSocket(0);
        System.out.println("HOSTING");
        System.out.println(InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort());
        clientListener = CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    playerCount++;
                    TetrisServerThread thread = new TetrisServerThread(serverSocket.accept(), nextID, playerCount, this, running.get());
                    thread.start();
                    serverThreads.put(nextID, thread);
                    //TODO tetris.setWaitingPlayers(playerCount);
                    nextID++;
                    System.out.println("CONNECTION");
                } catch (SocketException ignored) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
    }

    public static TetrisServer createServer(Tetris tetris) {
        try {
            return new TetrisServer(tetris);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: Failed to create server!");
            return null;
        }
    }

    protected synchronized void removeServerThread(int id) {
        serverThreads.remove(id);
        activePlayers.remove(id);
        playerCount--;
        //TODO tetris.setWaitingPlayers(playerCount);
    }

    public synchronized void distributeLines(int senderID, int amount) {
        int recieverID = activePlayers.get(gen.nextInt(activePlayers.size() - 1));
        if (recieverID == senderID)
            recieverID = activePlayers.getLast();
        if (recieverID == 0)
            ;//TODO host recieves
        else
            serverThreads.get(recieverID).sendLines(amount);
    }

    public synchronized void removeActivePlayer(int id) {
        activePlayers.remove(id);
        if (activePlayers.size() == 0)
            endGame(id);
    }

    private void endGame(int winnerID) {
        running.set(false);
        serverThreads.values().forEach(t -> t.endGame(winnerID));
        //TODO host
    }

    public synchronized void startGame(int seed) {
        //TODO do stuff
        activePlayers.add(0);
        activePlayers.addAll(serverThreads.keySet());
        running.set(true);
    }

    public void shutdown() {
        serverThreads.values().forEach(TetrisServerThread::shutdown);
        serverThreads.clear();
        activePlayers.clear();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}