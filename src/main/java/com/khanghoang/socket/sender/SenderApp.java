package com.khanghoang.socket.sender;

import com.khanghoang.socket.config.SenderConfig;
import com.khanghoang.socket.sender.network.SocketServer;

import java.io.File;

public class SenderApp {
    public static void main(String[] args) {
        SocketServer server = new SocketServer(SenderConfig.PORT);
        server.startAsync();

        server.waitForClients(SenderConfig.PORT);

        for (String path : SenderConfig.FILES) {
            server.distributeFile(new File(path));
        }

    }
}
