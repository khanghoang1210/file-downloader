package com.khanghoang.filedownloader.sender;

import com.khanghoang.filedownloader.config.AppConfig;
import com.khanghoang.filedownloader.sender.coordinator.FileTransferCoordinator;
import com.khanghoang.filedownloader.sender.core.*;
import com.khanghoang.filedownloader.sender.distributor.FileDistributor;
import com.khanghoang.filedownloader.shared.impl.DefaultFileManager;
import com.khanghoang.filedownloader.shared.interfaces.FileManager;

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


        // ====== Run File Transfer ======
        for (String path : AppConfig.FILES) {
            new Thread(()->{
                FileTransferCoordinator coordinator = new FileTransferCoordinator(
                        clientManager,
                        fileDistributor,
                        fileManager,
                        expectedClients
                );
                coordinator.sendAsync(path);
            }).start();
        }


        System.out.println("🚀 Server started. Waiting for " + expectedClients + " clients...");
    }
}
