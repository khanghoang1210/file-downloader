package com.khanghoang.socket.sender;

import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.sender.network.SocketServer;

import java.io.File;

public class SenderApp {
    public static void main(String[] args) {
        SocketServer server = new SocketServer(AppConfig.PORT);
        server.startAsync();

        server.waitForClients(AppConfig.EXPECTED_CLIENTS);

        for (String path : AppConfig.FILES) {
            server.distributeFile(new File(path));
        }

    }
}
