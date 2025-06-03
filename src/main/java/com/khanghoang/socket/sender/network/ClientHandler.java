package com.khanghoang.socket.sender.network;

import com.khanghoang.socket.shared.Protocol;
import com.khanghoang.socket.shared.model.ProtocolChunk;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final DataOutputStream dos;
    private final BlockingQueue<ProtocolChunk> chunkQueue = new LinkedBlockingQueue<>();
    private final int clientId;

    public ClientHandler(Socket socket, int clientId) throws IOException {
        this.clientSocket = socket;
        this.clientId = clientId;
        this.dos = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void enqueueChunk(ProtocolChunk chunk) {
        chunkQueue.offer(chunk);
    }

    @Override
    public void run() {
        try {
            while (true) {
                ProtocolChunk chunk = chunkQueue.take();
                Protocol.encodeChunk(dos, chunk);
                System.out.println("Client #" + clientId + " sent chunk " +
                        chunk.getChunkIndex() + "/" + chunk.getTotalChunks() +
                        " of file " + chunk.getFileName());
            }
        } catch (Exception e) {
            System.out.println("Client #" + clientId + " disconnected or error: " + e.getMessage());
        }
    }
}
