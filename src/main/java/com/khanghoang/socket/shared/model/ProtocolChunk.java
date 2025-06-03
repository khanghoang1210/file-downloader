package com.khanghoang.socket.shared.model;


public class ProtocolChunk {
    private final String fileName;
    private final int chunkIndex;
    private int totalChunks;
    private final long chunkSize;
    private final byte[] data;

    public ProtocolChunk(String fileName, int chunkIndex, int totalChunks, long chunkSize, byte[] data) {
        this.fileName = fileName;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.chunkSize = chunkSize;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }
}