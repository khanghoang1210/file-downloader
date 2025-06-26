package com.khanghoang.filedownloader.receiver.solution;

import com.khanghoang.filedownloader.config.AppConfig;
import com.khanghoang.filedownloader.receiver.assembler.FileAssembler;
import com.khanghoang.filedownloader.receiver.core.DefaultSocketClientManager;
import com.khanghoang.filedownloader.receiver.core.SocketClientManager;
import com.khanghoang.filedownloader.receiver.service.ReceiverService;
import com.khanghoang.filedownloader.shared.impl.DefaultFileManager;

public class BaseReceiverSolution  implements Solution{
    @Override
    public void solve() {
        int numClients = AppConfig.NUM_CLIENTS;
        for (int i = 0; i < numClients; i++) {
            new Thread(() -> {
                try {
                    SocketClientManager socketClientManager =
                            new DefaultSocketClientManager("localhost", AppConfig.PORT);
                    socketClientManager.connect();
                    FileAssembler assembler = new FileAssembler(new DefaultFileManager(), AppConfig.OUT_DIR);
                    ReceiverService receiver = new ReceiverService(socketClientManager, assembler);

                    while (true) {
                        receiver.handleChunk();
                    }

                } catch (Exception e) {
                    System.err.println("Client thread error: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
