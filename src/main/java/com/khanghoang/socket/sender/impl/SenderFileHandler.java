package com.khanghoang.socket.sender.impl;

import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.shared.interfaces.FileReader;
import com.khanghoang.socket.shared.model.ProtocolChunk;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SenderFileHandler implements FileReader {
    private final String outputDir;
    private final List<Consumer<ProtocolChunk>> chunkConsumers;

    public SenderFileHandler(String outputDir, List<Consumer<ProtocolChunk>> chunkConsumers) {
        this.outputDir = outputDir;
        this.chunkConsumers = chunkConsumers;
    }

    @Override
    public void readFile(File file, Consumer<byte[]> chunkConsumer) throws IOException {
        if (chunkConsumers.isEmpty()) {
            System.out.println("No consumers available.");
            return;
        }

        byte[] fileData;
        try (FileInputStream fis = new FileInputStream(file)) {
            fileData = fis.readAllBytes();
        }

        int chunkSize = AppConfig.CHUNK_SIZE;
        int totalChunks = (int) Math.ceil((double) fileData.length / chunkSize);
        int consumerCount = chunkConsumers.size();

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

            // Distribute to consumers using round-robin
            Consumer<ProtocolChunk> consumer = chunkConsumers.get(i % consumerCount);
            consumer.accept(chunk);
        }

        System.out.println("Distributed file: " + file.getName() + " into " + totalChunks + " chunks.");
    }

    @Override
    public String getOutputDir() {
        return outputDir;
    }
} 