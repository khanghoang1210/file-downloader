package com.khanghoang.socket.shared.impl;

import com.khanghoang.socket.shared.interfaces.NetworkHandler;
import com.khanghoang.socket.shared.interfaces.ProtocolHandler;
import com.khanghoang.socket.shared.model.ProtocolChunk;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultProtocolHandler implements ProtocolHandler {
    private final NetworkHandler networkHandler;

    public DefaultProtocolHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    @Override
    public void encodeChunk(ProtocolChunk chunk) throws IOException {
        byte[] fileNameBytes = chunk.getFileName().getBytes(StandardCharsets.UTF_8);
        
        // Write file name length and data
        networkHandler.send(intToBytes(fileNameBytes.length));
        networkHandler.send(fileNameBytes);
        
        // Write chunk metadata
        networkHandler.send(intToBytes(chunk.getChunkIndex()));
        networkHandler.send(intToBytes(chunk.getTotalChunks()));
        networkHandler.send(intToBytes(chunk.getData().length));
        
        // Write chunk data
        networkHandler.send(chunk.getData());
    }

    @Override
    public ProtocolChunk decodeChunk() throws IOException {
        // Read file name
        int fileNameLength = bytesToInt(networkHandler.receive());
        byte[] fileNameBytes = networkHandler.receive();
        String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);

        // Read chunk metadata
        int chunkIndex = bytesToInt(networkHandler.receive());
        int totalChunks = bytesToInt(networkHandler.receive());
        int dataLength = bytesToInt(networkHandler.receive());
        
        // Read chunk data
        byte[] data = networkHandler.receive();

        return new ProtocolChunk(fileName, chunkIndex, totalChunks, dataLength, data);
    }

    private byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value
        };
    }

    private int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) |
               (bytes[3] & 0xFF);
    }
} 