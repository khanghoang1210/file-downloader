package com.khanghoang.socket.receiver.core;

import com.khanghoang.socket.shared.interfaces.FileWriter;
import com.khanghoang.socket.shared.interfaces.ProtocolHandler;
import com.khanghoang.socket.shared.model.ProtocolChunk;
import java.io.IOException;

public class ReceiverSession {
    private final FileWriter fileWriter;
    private final ProtocolHandler protocolHandler;

    public ReceiverSession(FileWriter fileWriter, ProtocolHandler protocolHandler) {
        this.fileWriter = fileWriter;
        this.protocolHandler = protocolHandler;
    }

    public void handleChunk(ProtocolChunk chunk) throws IOException {
        System.out.println("Handling chunk " + chunk.getChunkIndex() + " of " + chunk.getTotalChunks() + " for file " + chunk.getFileName());
        fileWriter.assembleFile(chunk.getFileName(), chunk.getTotalChunks());
        fileWriter.writeChunk(chunk.getFileName(), chunk.getChunkIndex(), chunk.getData());
    }

    public void start() throws IOException {
        while (true) {
            ProtocolChunk chunk = protocolHandler.decodeChunk();
            if (chunk == null) break;
            System.out.println("Received: " + chunk.getFileName());
            handleChunk(chunk);
        }
    }
}
