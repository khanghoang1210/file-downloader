package com.khanghoang.filedownloader.receiver.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DefaultSocketClientManager implements SocketClientManager{
    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private boolean connected = false;
    private static final int MAX_BUFFER_SIZE = 1024 * 1024; // 1MB

    public DefaultSocketClientManager(String host, int port) {
        this.host = host;
        this.port = port;
    }
    @Override
    public void connect() throws IOException {
        if (connected) {
            throw new IllegalStateException("Already connected");
        }
        socket = new Socket(host, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        connected = true;
    }

    @Override
    public void disconnect() throws IOException {
        if (!connected) {
            return; // Ignore if already disconnected
        }
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } finally {
            connected = false;
        }
    }

    @Override
    public byte[] receive() throws IOException {
        if (!connected) {
            throw new IllegalStateException("Not connected");
        }
        try {
            int length = input.readInt();
            if (length <= 0 || length > MAX_BUFFER_SIZE) {
                throw new IOException("Invalid data length: " + length);
            }
            byte[] data = new byte[length];
            input.readFully(data);
            return data;
        } catch (IOException e) {
            connected = false;
            throw e;
        }
    }
}
