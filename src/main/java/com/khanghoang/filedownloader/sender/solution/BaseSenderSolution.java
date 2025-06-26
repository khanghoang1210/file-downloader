package com.khanghoang.filedownloader.sender.solution;

import com.khanghoang.filedownloader.config.AppConfig;
import com.khanghoang.filedownloader.sender.coordinator.FileTransferCoordinator;
import com.khanghoang.filedownloader.sender.core.ClientManager;
import com.khanghoang.filedownloader.sender.core.DefaultClientManager;
import com.khanghoang.filedownloader.sender.core.DefaultSocketServerManager;
import com.khanghoang.filedownloader.sender.core.SocketServerManager;
import com.khanghoang.filedownloader.sender.distributor.FileDistributor;
import com.khanghoang.filedownloader.shared.impl.DefaultFileManager;
import com.khanghoang.filedownloader.shared.interfaces.FileManager;

public class BaseSenderSolution implements Solution{
    @Override
    public void solve() {
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
