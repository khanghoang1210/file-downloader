package com.khanghoang.socket.receiver.impl;

import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.shared.interfaces.FileWriter;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ReceiverFileHandler implements FileWriter {
    private final String outputDir;
    private final String tempDir;
    private final Map<String, Integer> expectedChunkCounts = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> fileLocks = new ConcurrentHashMap<>();
    private final Map<String, boolean[]> receivedChunks = new ConcurrentHashMap<>();

    public ReceiverFileHandler(String outputDir) {
        this.outputDir = outputDir;
        this.tempDir = outputDir + "temp" + File.separator;
        new File(tempDir).mkdirs();
    }

    @Override
    public void writeChunk(String fileName, int chunkIndex, byte[] data) throws IOException {
        fileLocks.putIfAbsent(fileName, new ReentrantLock());
        ReentrantLock lock = fileLocks.get(fileName);

        lock.lock();
        try {
            ensureReceivedChunkArray(fileName, chunkIndex);
            File chunkFile = new File(tempDir + fileName + ".part" + chunkIndex);
            try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
                fos.write(data);
            }
            System.out.println("Saved chunk " + chunkIndex + " of " + fileName);

            // Mark chunk received
            receivedChunks.get(fileName)[chunkIndex] = true;

            // Attempt to assemble
            tryAssembleIfComplete(fileName);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void assembleFile(String fileName, int totalChunks) throws IOException {
        fileLocks.putIfAbsent(fileName, new ReentrantLock());
        ReentrantLock lock = fileLocks.get(fileName);

        lock.lock();
        try {
            expectedChunkCounts.putIfAbsent(fileName, totalChunks);

            boolean[] existing = receivedChunks.get(fileName);
            if (existing == null) {
                receivedChunks.put(fileName, new boolean[totalChunks]);
            }

            tryAssembleIfComplete(fileName);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getOutputDir() {
        return outputDir;
    }

    @Override
    public String getTempDir() {
        return tempDir;
    }

    private void ensureReceivedChunkArray(String fileName, int chunkIndex) {
        fileLocks.putIfAbsent(fileName, new ReentrantLock());
        ReentrantLock lock = fileLocks.get(fileName);
        lock.lock();
        try {
            boolean[] chunks = receivedChunks.get(fileName);
            if (chunks == null) {
                int size = Math.max(chunkIndex + 1, 16);
                chunks = new boolean[size];
                receivedChunks.put(fileName, chunks);
            } else if (chunkIndex >= chunks.length) {
                boolean[] expanded = new boolean[Math.max(chunkIndex + 1, chunks.length * 2)];
                System.arraycopy(chunks, 0, expanded, 0, chunks.length);
                receivedChunks.put(fileName, expanded);
            }
        } finally {
            lock.unlock();
        }
    }


    private void tryAssembleIfComplete(String fileName) throws IOException {
        Integer totalChunks = expectedChunkCounts.get(fileName);
        boolean[] chunks = receivedChunks.get(fileName);

        if (totalChunks == null || chunks == null) {
            System.out.println("Waiting for totalChunks or chunks for " + fileName);
            return;
        }

        int received = countReceivedChunks(chunks);
        System.out.println("Checking completion for " + fileName + ": " + received + "/" + totalChunks + " chunks received");

        if (received >= totalChunks && isComplete(chunks, totalChunks)) {
            System.out.println("All chunks received for " + fileName + ", assembling...");
            doAssembleFile(fileName, totalChunks);
            cleanupState(fileName);
        }
    }

    private boolean isComplete(boolean[] chunks, int totalChunks) {
        if (chunks.length < totalChunks) return false;
        for (int i = 0; i < totalChunks; i++) {
            if (!chunks[i]) return false;
        }
        return true;
    }

    private int countReceivedChunks(boolean[] chunks) {
        int count = 0;
        for (boolean b : chunks) {
            if (b) count++;
        }
        return count;
    }

    private void cleanupState(String fileName) {
        expectedChunkCounts.remove(fileName);
        receivedChunks.remove(fileName);
        fileLocks.remove(fileName);
    }

    private void doAssembleFile(String fileName, int totalChunks) throws IOException {
        File outputFile = new File(outputDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (int i = 0; i < totalChunks; i++) {
                File partFile = new File(tempDir + fileName + ".part" + i);
                if (!partFile.exists()) throw new IOException("Missing chunk file: " + partFile.getName());

                waitUntilReadable(partFile);

                try (FileInputStream fis = new FileInputStream(partFile)) {
                    byte[] buffer = new byte[AppConfig.BUFFER_SIZE];
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                    }
                }

                if (!partFile.delete()) {
                    System.err.println("Warning: Could not delete temp file " + partFile.getName());
                }
            }
        }
        System.out.println("File assembled at: " + outputFile.getAbsolutePath());
    }

    private void waitUntilReadable(File file) throws IOException {
        int retries = 0;
        while (!file.canRead() && retries < 5) {
            try {
                Thread.sleep(100);
                retries++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for file", e);
            }
        }
        if (!file.canRead()) {
            throw new IOException("Cannot read chunk file after retries: " + file.getName());
        }
    }
}
