package com.khanghoang.socket.receiver.core;

import java.io.IOException;

public interface SocketClientManager {
     void connect() throws IOException;
     void disconnect() throws IOException;
     byte[] receive() throws IOException;
}
