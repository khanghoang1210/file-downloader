package com.khanghoang.socket.receiver;

import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.receiver.assembler.FileAssembler;
import com.khanghoang.socket.receiver.core.DefaultSocketClientManager;
import com.khanghoang.socket.receiver.core.SocketClientManager;
import com.khanghoang.socket.receiver.service.ReceiverService;
import com.khanghoang.socket.shared.impl.DefaultFileManager;

public class Main {
    public static void main(String[] args) {
        int numClients = AppConfig.NUM_CLIENTS;
        for (int i = 0; i < numClients; i++) {
            new Thread(() -> {
                try {
                    SocketClientManager socketClientManager =
                            new DefaultSocketClientManager("localhost", AppConfig.PORT);
                    socketClientManager.connect();
                    FileAssembler assembler = new FileAssembler(new DefaultFileManager(), AppConfig.OUT_DIR);
                    ReceiverService receiver = new ReceiverService(socketClientManager, assembler);

                    receiver.handleChunk();

                    socketClientManager.disconnect();
                } catch (Exception e) {
                    System.err.println("Client thread error: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
