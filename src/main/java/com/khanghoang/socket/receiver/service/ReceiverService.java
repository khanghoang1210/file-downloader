package com.khanghoang.socket.receiver.service;

import com.khanghoang.socket.receiver.assembler.FileAssembler;
import com.khanghoang.socket.receiver.core.SocketClientManager;
import com.khanghoang.socket.shared.model.ProtocolChunk;

import java.io.IOException;

public class ReceiverService {
    private final SocketClientManager clientManager;
    private final FileAssembler fileAssembler;

    public ReceiverService(SocketClientManager clientManager, FileAssembler fileAssembler) {
        this.clientManager = clientManager;
        this.fileAssembler = fileAssembler;
    }

    public void handleChunk() {
        try {
            byte[] raw = clientManager.receive();

            ProtocolChunk chunk = ProtocolChunk.decode(raw);
            fileAssembler.storeChunk(chunk);
        } catch (IOException e) {
            System.err.println("Failed to receive and store chunk: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

