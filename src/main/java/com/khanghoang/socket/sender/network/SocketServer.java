package com.khanghoang.socket.sender.network;

import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.sender.impl.SenderFileHandler;
import com.khanghoang.socket.shared.interfaces.FileReader;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SocketServer {
    private final int port;
    private final List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private int clientCounter = 0;

    public SocketServer(int port) {
        this.port = port;
    }

    public void startAsync() {
        new Thread(this::start).start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, clientCounter++);
                clientHandlers.add(handler);
                executor.execute(handler);
                System.out.println("Client connected: #" + (clientCounter - 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void distributeFile(File file) {
        try {
            List<Consumer<com.khanghoang.socket.shared.model.ProtocolChunk>> consumers = new ArrayList<>();
            for (ClientHandler handler : clientHandlers) {
                consumers.add(handler::enqueueChunk);
            }
            
            FileReader fileReader = new SenderFileHandler(AppConfig.OUT_DIR, consumers);
            fileReader.readFile(file, null); // chunkConsumer is not used as we use ProtocolChunk consumers
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitForClients(int expectedClients) {
        while (clientCounter < expectedClients) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Waiting for clients... Currently connected: " + clientCounter);
        }
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
}
