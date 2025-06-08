package com.khanghoang.socket.receiver.solution;

import com.khanghoang.socket.config.ReceiverConfig;
import com.khanghoang.socket.receiver.network.SocketClient;

public class MultiReceiver implements Receiver {
    private final int numClients;

    public MultiReceiver(int numClients) {
        this.numClients = numClients;
    }
    @Override
    public void run() {
        for (int i = 0; i < numClients; i++) {
            new Thread(() -> {
                try {
                    ReceiverSession session = new ReceiverSession(ReceiverConfig.OUT_DIR);
                    SocketClient client = new SocketClient("localhost", 9000, session);
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
