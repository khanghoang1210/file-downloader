package com.khanghoang.socket.sender.core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DefaultSocketServerManager implements SocketServerManager {
    private final int port;
    private final ClientManager clientManager;

    public DefaultSocketServerManager(int port, ClientManager clientManager) {
        this.port = port;
        this.clientManager = clientManager;
    }


    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientManager.onClientConnected(clientSocket);
                System.out.println("Client connected: #");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        // Đóng socket
    }

    public void send(Socket socket, byte[] data) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
