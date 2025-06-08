package com.khanghoang.socket.receiver.network;

import com.khanghoang.socket.receiver.helper.FileAssembler;
import com.khanghoang.socket.receiver.solution.ReceiverSession;
import com.khanghoang.socket.shared.Protocol;
import com.khanghoang.socket.shared.model.ProtocolChunk;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketClient {
    private final String host;
    private final int port;
    private Socket socket;
    private final ReceiverSession session;

    public SocketClient(String host, int port, ReceiverSession session) {
        this.host = host;
        this.port = port;
        this.session = session;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);
            System.out.println("Connected to server: " + host + ":" + port);

            try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                while (!socket.isClosed()) {
                    ProtocolChunk chunk = Protocol.decodeChunk(dis);
                    if (chunk == null) break;
                    session.handleChunk(chunk);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("Connection closed.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Connection closed.");
        }
    }
}
