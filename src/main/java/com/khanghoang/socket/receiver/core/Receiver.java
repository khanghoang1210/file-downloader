package com.khanghoang.socket.receiver.core;

import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.receiver.network.SocketClient;

public class Receiver {
    private final int numClients;

    public Receiver(int numClients) {
        this.numClients = numClients;
    }

    public void run() {
        for (int i = 0; i < numClients; i++) {
            new Thread(() -> {
                try {
                    ReceiverSession session = new ReceiverSession(AppConfig.OUT_DIR);
                    SocketClient client = new SocketClient("localhost", 9000, session);
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
