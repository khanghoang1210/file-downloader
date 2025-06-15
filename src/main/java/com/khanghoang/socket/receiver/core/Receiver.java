package com.khanghoang.socket.receiver.core;

import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.receiver.impl.ReceiverFileHandler;
import com.khanghoang.socket.receiver.network.SocketClient;
import com.khanghoang.socket.shared.impl.DefaultProtocolHandler;
import com.khanghoang.socket.shared.impl.SocketNetworkHandler;
import com.khanghoang.socket.shared.interfaces.FileWriter;
import com.khanghoang.socket.shared.interfaces.NetworkHandler;
import com.khanghoang.socket.shared.interfaces.ProtocolHandler;

public class Receiver {
    private final int numClients;

    public Receiver(int numClients) {
        this.numClients = numClients;
    }

    public void run() {
        for (int i = 0; i < numClients; i++) {
            new Thread(() -> {
                try {
                    FileWriter fileHandler = new ReceiverFileHandler(AppConfig.OUT_DIR);
                    NetworkHandler networkHandler = new SocketNetworkHandler("localhost", AppConfig.PORT);
                    networkHandler.connect();
                    
                    ProtocolHandler protocolHandler = new DefaultProtocolHandler(networkHandler);
                    ReceiverSession session = new ReceiverSession(fileHandler, protocolHandler);
                    session.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
