package com.khanghoang.socket.receiver.core;

import com.khanghoang.socket.receiver.helper.FileAssembler;
import com.khanghoang.socket.shared.model.ProtocolChunk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReceiverSession {
    private final String outputDir;
    private final String tempDir;
    private final Map<String, Integer> expectedChunkCounts = new ConcurrentHashMap<>();

    public ReceiverSession(String outputDir) {
        this.outputDir = outputDir;
        this.tempDir = outputDir + "temp\\";
        new File(tempDir).mkdirs();
    }

    public void handleChunk(ProtocolChunk chunk) throws IOException {
        String fileName = chunk.getFileName();
        int chunkIndex = chunk.getChunkIndex();
        int totalChunks = chunk.getTotalChunks();

        expectedChunkCounts.putIfAbsent(fileName, totalChunks);
        saveChunkToTempFile(chunk);

        // Check if we received all chunks
        if (isComplete(fileName)) {
            assembleFile(fileName);
        }
    }

    private void saveChunkToTempFile(ProtocolChunk chunk) throws IOException {
        String fileName = chunk.getFileName();
        int chunkIndex = chunk.getChunkIndex();

        File chunkFile = new File(tempDir + fileName + ".part" + chunkIndex);
        try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
            fos.write(chunk.getData());
        }

        System.out.println("Saved chunk " + chunkIndex + "/" + chunk.getTotalChunks() + " of " + fileName);
    }

    private boolean isComplete(String fileName) {
        File dir = new File(tempDir);
        int expected = expectedChunkCounts.get(fileName);
        int actual = (int) dir.list((d, name) -> name.startsWith(fileName + ".part")).length;
        return actual == expected;
    }

    private void assembleFile(String fileName) throws IOException {
        FileAssembler.assembleFile(fileName, expectedChunkCounts.get(fileName), tempDir, outputDir);
        expectedChunkCounts.remove(fileName);
    }
}
