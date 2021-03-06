package main.multiplayer;

import main.gameHandler.MultiplayerGameHost;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TetrisServer {
    public final String hostName;
    private final ServerSocket serverSocket;
    private final Map<Integer, TetrisServerThread> serverThreads = new ConcurrentHashMap<>();
    private final LinkedList<Integer> activePlayers = new LinkedList<>();
    private final Random gen = new Random((int) (Math.random() * 1000));
    private final AtomicBoolean running = new AtomicBoolean(false);
    private int nextID = 1; //ID 0 = host
    private int playerCount = 1;
    private MultiplayerGameHost multiplayerGameHost;
    private int hostRank = 0;

    private TetrisServer() throws IOException {
        hostName = getHostname();
        serverSocket = new ServerSocket(0);
        System.out.println("HOSTING (" + hostName + ":" + serverSocket.getLocalPort() + ")");
        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    TetrisServerThread thread = new TetrisServerThread(serverSocket.accept(), nextID, playerCount + 1, this, running.get());
                    playerCount++;
                    thread.start();
                    multiplayerGameHost.setPlayerCount(playerCount);
                    serverThreads.values().forEach(t -> t.setPlayerCount(playerCount));
                    serverThreads.put(nextID, thread);
                    nextID++;
                    System.out.println("CONNECTION");
                } catch (SocketException ignored) {
                    break; //Is thrown when the socket is closed.
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
    }

    private static String getHostname() {
        Enumeration<NetworkInterface> ne;
        try {
            ne = NetworkInterface.getNetworkInterfaces();
            while (ne.hasMoreElements()) {
                for (InterfaceAddress f : ne.nextElement().getInterfaceAddresses())
                    if (f.getAddress().isSiteLocalAddress())
                        if (!f.getAddress().toString().equals("192.168.56.1"))
                            return f.getAddress().getHostAddress();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public static TetrisServer createServer() {
        try {
            return new TetrisServer();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: Failed to create server!");
            return null;
        }
    }

    protected synchronized void removeServerThread(Integer id) {
        serverThreads.remove(id);
        removeActivePlayer(id);
        playerCount--;
        multiplayerGameHost.setPlayerCount(playerCount);
        serverThreads.values().forEach(t -> t.setPlayerCount(playerCount));
    }

    public synchronized void distributeLines(int senderID, int amount) {
        if (activePlayers.size() > 1) {
            int receiverID = activePlayers.get(gen.nextInt(activePlayers.size() - 1));
            if (receiverID == senderID)
                receiverID = activePlayers.getLast();
            if (receiverID == 0)
                multiplayerGameHost.receiveLines(amount);
            else
                serverThreads.get(receiverID).sendLines(amount);
        }
    }

    public synchronized void removeActivePlayer(Integer id) {
        if (id == 0)
            hostRank = activePlayers.size();
        activePlayers.remove(id);
        if (activePlayers.size() == 0) {
            endGame(id);
            return;
        }
        activePlayers.stream().filter(i -> i != 0).forEach(i -> serverThreads.get(i).playerDied());
    }

    private void endGame(int winnerID) {
        running.set(false);
        serverThreads.values().forEach(t -> t.endGame(winnerID));
        multiplayerGameHost.end(winnerID);
    }

    public synchronized void startGame(int seed) {
        activePlayers.add(0);
        activePlayers.addAll(serverThreads.keySet());
        running.set(true);
        serverThreads.values().forEach(t -> t.startGame(seed));
    }

    public void shutdown() {
        running.set(false);
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
        return hostName;
    }

    public int getRank() {
        return hostRank;
    }

    public void setMultiplayerGameHost(MultiplayerGameHost multiplayerGameHost) {
        this.multiplayerGameHost = multiplayerGameHost;
    }
}