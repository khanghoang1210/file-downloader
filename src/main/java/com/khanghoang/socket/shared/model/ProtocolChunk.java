package com.khanghoang.socket.shared.model;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

    public byte[] encode() {
        byte[] fileNameBytes = this.getFileName().getBytes(StandardCharsets.UTF_8);
        byte[] data = this.getData();

        int totalLength = Integer.BYTES                             // fileName length
                + fileNameBytes.length                     // fileName bytes
                + Integer.BYTES                            // chunkIndex
                + Integer.BYTES                            // totalChunks
                + Long.BYTES                               // chunkSize
                + Integer.BYTES                            // data length
                + data.length;                             // data

        ByteBuffer buffer = ByteBuffer.allocate(totalLength);

        buffer.putInt(fileNameBytes.length);
        buffer.put(fileNameBytes);

        buffer.putInt(this.getChunkIndex());
        buffer.putInt(this.getTotalChunks());
        buffer.putLong(this.getChunkSize());

        buffer.putInt(data.length);
        buffer.put(data);

        return buffer.array();
    }

    public static ProtocolChunk decode(byte[] raw) {
        ByteBuffer buffer = ByteBuffer.wrap(raw);

        int fileNameLength = buffer.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        buffer.get(fileNameBytes);
        String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);

        int chunkIndex = buffer.getInt();
        int totalChunks = buffer.getInt();
        long chunkSize = buffer.getLong();

        int dataLength = buffer.getInt();
        byte[] data = new byte[dataLength];
        buffer.get(data);

        return new ProtocolChunk(fileName, chunkIndex, totalChunks, chunkSize, data);
    }
}