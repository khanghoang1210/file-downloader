package com.khanghoang.socket.sender.service;

import com.khanghoang.socket.sender.network.ClientHandler;
import com.khanghoang.socket.shared.model.ProtocolChunk;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

public class FileDistributorService {

    public static void distributeFile(File file, List<ClientHandler> handlers, int chunkSize) throws Exception {
        if (handlers.isEmpty()) {
            System.out.println("No clients connected.");
            return;
        }

        byte[] fileData;
        try (FileInputStream fis = new FileInputStream(file)) {
            fileData = fis.readAllBytes();
        }

        int totalChunks = (int) Math.ceil((double) fileData.length / chunkSize);
        int handlerCount = handlers.size();

        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, fileData.length);
            byte[] chunkData = Arrays.copyOfRange(fileData, start, end);

            ProtocolChunk chunk = new ProtocolChunk(
                    file.getName(),
                    i,
                    totalChunks,
                    chunkData.length,
                    chunkData
            );

            // Gán mỗi chunk cho client theo round-robin hoặc chia đều
            ClientHandler handler = handlers.get(i % handlerCount);
            handler.enqueueChunk(chunk);
        }

        System.out.println("Distributed file: " + file.getName() + " into " + totalChunks + " chunks.");
    }
}
