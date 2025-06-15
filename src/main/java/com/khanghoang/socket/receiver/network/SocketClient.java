package com.khanghoang.socket.receiver.network;

import com.khanghoang.socket.receiver.core.ReceiverSession;
import com.khanghoang.socket.shared.impl.DefaultProtocolHandler;
import com.khanghoang.socket.shared.impl.SocketNetworkHandler;
import java.io.IOException;

public class SocketClient {
    private final String host;
    private final int port;
    private final ReceiverSession session;
    private final SocketNetworkHandler networkHandler;
    private final DefaultProtocolHandler protocolHandler;

    public SocketClient(String host, int port, ReceiverSession session) {
        this.host = host;
        this.port = port;
        this.session = session;
        this.networkHandler = new SocketNetworkHandler(host, port);
        this.protocolHandler = new DefaultProtocolHandler(networkHandler);
    }

    public void connect() {
        try {
            networkHandler.connect();
            System.out.println("Connected to server: " + host + ":" + port);
            session.start();
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            try {
                networkHandler.disconnect();
                System.out.println("Connection closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        networkHandler.disconnect();
        System.out.println("Connection closed.");
    }
}
