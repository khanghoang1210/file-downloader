package com.khanghoang.socket.sender;

import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.sender.coordinator.FileTransferCoordinator;
import com.khanghoang.socket.sender.core.*;
import com.khanghoang.socket.sender.distributor.FileDistributor;
import com.khanghoang.socket.shared.impl.DefaultFileManager;
import com.khanghoang.socket.shared.interfaces.FileManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // ====== Dependency Setup ======
        ClientManager clientManager = new DefaultClientManager();
        SocketServerManager socketServerManager = new DefaultSocketServerManager(AppConfig.PORT, clientManager);

        new Thread(socketServerManager::start).start();

        FileManager fileManager = new DefaultFileManager();
        FileDistributor fileDistributor = new FileDistributor(socketServerManager);

        // ====== Coordinator ======
        int expectedClients = AppConfig.EXPECTED_CLIENTS;
        FileTransferCoordinator coordinator = new FileTransferCoordinator(
                clientManager,
                fileDistributor,
                fileManager,
                expectedClients
        );

        // ====== Run File Transfer ======
        for (String path : AppConfig.FILES) {
            coordinator.send(path);
        }


        System.out.println("🚀 Server started. Waiting for " + expectedClients + " clients...");
    }
}
