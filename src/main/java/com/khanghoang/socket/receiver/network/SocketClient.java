package com.khanghoang.socket.receiver.network;

import com.khanghoang.socket.receiver.service.FileAssemblerService;
import com.khanghoang.socket.shared.Protocol;
import com.khanghoang.socket.shared.model.ProtocolChunk;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketClient {
    private final String host;
    private final int port;
    private Socket socket;

    // Bộ nhớ RAM để lưu chunk theo từng file
    private static final Map<String, Map<Integer, byte[]>> fileChunkBuffers = new ConcurrentHashMap<>();
    private static final Map<String, Integer> fileExpectedChunks = new ConcurrentHashMap<>();
    // Đường dẫn thư mục đầu ra mặc định
    private static String outputDir = "C:\\Users\\Ms.Trang\\Downloads\\test\\";

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void connect() throws IOException {
        socket = new Socket(host, port);
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            System.out.println("Connected to server: " + host + ":" + port);

            while (!socket.isClosed() && socket.isConnected()) {
                ProtocolChunk chunk = Protocol.decodeChunk(dis);
                if (chunk == null) break;

                processChunkInMemory(chunk);

                String fileName = chunk.getFileName();
                if (fileChunkBuffers.get(fileName).size() == fileExpectedChunks.get(fileName)) {
                    assembleAndSaveFile(fileName);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void processChunkInMemory(ProtocolChunk chunk) {
        String fileName = chunk.getFileName();
        int chunkIndex = chunk.getChunkIndex();
        int totalChunks = chunk.getTotalChunks();

        fileChunkBuffers.putIfAbsent(fileName, new ConcurrentHashMap<>());
        fileExpectedChunks.putIfAbsent(fileName, totalChunks);

        fileChunkBuffers.get(fileName).put(chunkIndex, chunk.getData());

        System.out.println("Received chunk " + chunkIndex + "/" + totalChunks + " of " + fileName);
    }

    private synchronized void assembleAndSaveFile(String fileName) {
        try {
            Map<Integer, byte[]> buffer = fileChunkBuffers.get(fileName);
            int totalChunks = fileExpectedChunks.get(fileName);

            FileAssemblerService.assembleFile(fileName, totalChunks, buffer, outputDir);
            System.out.println("File assembled at: " + outputDir + fileName);

            // Xóa bộ nhớ tạm
            fileChunkBuffers.remove(fileName);
            fileExpectedChunks.remove(fileName);
        } catch (IOException e) {
            System.err.println("Error assembling file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Connection closed.");
        }
    }
}
