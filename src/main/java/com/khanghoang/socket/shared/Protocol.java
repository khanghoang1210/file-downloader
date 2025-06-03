package com.khanghoang.socket.shared;

import com.khanghoang.socket.shared.model.ProtocolChunk;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Protocol {
    public static void encodeChunk(OutputStream out, ProtocolChunk chunk) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        byte[] fileNameBytes = chunk.getFileName().getBytes(StandardCharsets.UTF_8);

        dos.writeInt(fileNameBytes.length);
        dos.write(fileNameBytes);
        dos.writeInt(chunk.getChunkIndex());
        dos.writeInt(chunk.getTotalChunks());
        dos.writeInt(chunk.getData().length);
        dos.write(chunk.getData());
        dos.flush();
    }

    public static ProtocolChunk decodeChunk(DataInputStream in) throws IOException {
        int fileNameLength = in.readInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        in.readFully(fileNameBytes);
        String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);

        int chunkIndex = in.readInt();
        int totalChunks = in.readInt();
        int dataLength = in.readInt();
        byte[] data = new byte[dataLength];
        in.readFully(data);

        return new ProtocolChunk(fileName, chunkIndex, totalChunks, dataLength, data);
    }
}
