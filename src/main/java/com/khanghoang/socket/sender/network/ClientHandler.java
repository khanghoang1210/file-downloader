package com.khanghoang.socket.sender.network;

import com.khanghoang.socket.shared.impl.DefaultProtocolHandler;
import com.khanghoang.socket.shared.impl.SocketNetworkHandler;
import com.khanghoang.socket.shared.model.ProtocolChunk;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final int clientId;
    private final SocketNetworkHandler networkHandler;
    private final DefaultProtocolHandler protocolHandler;
    private final BlockingQueue<ProtocolChunk> chunkQueue = new LinkedBlockingQueue<>();

    public ClientHandler(Socket socket, int clientId) throws Exception {
        this.clientSocket = socket;
        this.clientId = clientId;
        this.networkHandler = new SocketNetworkHandler(socket);
        this.protocolHandler = new DefaultProtocolHandler(networkHandler);
    }

    public void enqueueChunk(ProtocolChunk chunk) {
        chunkQueue.offer(chunk);
    }

    @Override
    public void run() {
        try {
            while (true) {
                ProtocolChunk chunk = chunkQueue.take();
                protocolHandler.encodeChunk(chunk);
                System.out.println("Client #" + clientId + " sent chunk " +
                        chunk.getChunkIndex() + "/" + chunk.getTotalChunks() +
                        " of file " + chunk.getFileName());
                System.out.println("Client IP: " + clientSocket.getInetAddress());
            }
        } catch (Exception e) {
            System.out.println("Client #" + clientId + " disconnected or error: " + e.getMessage());
        } finally {
            try {
                if (networkHandler != null) {
                    networkHandler.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
