package com.khanghoang.filedownloader.sender.core;

import com.khanghoang.filedownloader.sender.coordinator.FileTransferCoordinator;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DefaultClientManager implements ClientManager {
    private final List<Socket> clients = new ArrayList<>();
    private final List<FileTransferCoordinator> listeners = new ArrayList<>();

    @Override
    public void onClientConnected(Socket client) {
        clients.add(client);
        notifyListeners();
    }

    @Override
    public List<Socket> getAllClients() {
        return new ArrayList<>(clients);
    }

    @Override
    public void subscribe(FileTransferCoordinator listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (FileTransferCoordinator listener : listeners) {
            listener.onClientConnected();
        }
    }
}
