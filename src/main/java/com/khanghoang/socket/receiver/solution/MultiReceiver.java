package com.khanghoang.socket.receiver.solution;

import com.khanghoang.socket.receiver.network.SocketClient;

public class MultiReceiver implements Receiver {
    @Override
    public void receive() {
        int numClients = 10;

        for (int i = 0; i < numClients; i++) {
            new Thread(() -> {
                try {
                    SocketClient client = new SocketClient("localhost", 9000);
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
