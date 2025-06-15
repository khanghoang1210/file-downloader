package com.khanghoang.socket.shared.interfaces;

import java.io.IOException;

public interface NetworkHandler {
    void connect() throws IOException;
    void disconnect() throws IOException;
    void send(byte[] data) throws IOException;
    byte[] receive() throws IOException;
    boolean isConnected();
} 