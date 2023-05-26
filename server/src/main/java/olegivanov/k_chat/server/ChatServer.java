package olegivanov.k_chat.server;

import olegivanov.network.Connection;
import olegivanov.network.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements ConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }

    private final List<Connection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server is running......");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection Exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
        sendAll("Client connected! WELCOME - " + connection);
    }

    @Override
    public synchronized void onReceiveString(Connection connection, String msg) {
        if (msg.equals("exit")) {
            connection.disconnect();
        }
        sendAll(connection + " says: " + msg);
    }

    @Override
    public synchronized void onDisconnect(Connection connection) {
        connections.remove(connection);
        sendAll("Client disconnected! GOODBYE - " + connection);
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        System.out.println("TCPConnection Exception: " + e);
    }

    private void sendAll(String value) {
        System.out.println(value);
        final int allI = connections.size();
        for (int i = 0; i < allI; i++) {
            connections.get(i).sendMsg(value);
        }


    }
}