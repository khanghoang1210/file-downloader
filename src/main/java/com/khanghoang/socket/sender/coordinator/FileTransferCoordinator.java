package com.khanghoang.socket.sender.coordinator;

import com.khanghoang.socket.sender.core.ClientManager;
import com.khanghoang.socket.sender.distributor.FileDistributor;
import com.khanghoang.socket.shared.interfaces.FileManager;

import java.io.File;
import java.net.Socket;
import java.util.List;

public class FileTransferCoordinator {
    private final ClientManager clientManager;
    private final FileManager fileManager;
    private final FileDistributor fileDistributor;
    private final int expectedClients;
    private boolean waitingToSend = false;
    private byte[] fileToSend = null;
    private String filePath = null;

    public FileTransferCoordinator(
            ClientManager clientManager,
            FileDistributor fileDistributor,
            FileManager fileManager,
            int expectedClients
    ) {
        this.clientManager = clientManager;
        this.fileDistributor = fileDistributor;
        this.fileManager = fileManager;
        this.expectedClients = expectedClients;


        clientManager.subscribe(this);
    }

    public void sendAsync(String filePath) {
        new Thread(()->{
            this.filePath = filePath;
            this.fileToSend = fileManager.read(filePath);
            this.waitingToSend = true;
            tryDistribute();
        }).start();
    }

    public void onClientConnected() {
        tryDistribute();
    }

    private void tryDistribute() {
        if (!waitingToSend || fileToSend == null) return;

        List<Socket> clients = clientManager.getAllClients();
        System.out.println("Currently connected: " + clients.size());

        if (clients.size() >= expectedClients) {
            waitingToSend = false;
            String fileName = new File(filePath).getName();
            fileDistributor.distributeTo(clients, fileToSend, fileName);
        }
    }
}
