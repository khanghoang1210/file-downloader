package com.khanghoang.socket.sender.core;

import com.khanghoang.socket.sender.coordinator.FileTransferCoordinator;

import java.net.Socket;
import java.util.List;

public interface ClientManager {
    void onClientConnected(Socket client);
    void subscribe(FileTransferCoordinator listener);
    List<Socket> getAllClients();
}
