package com.khanghoang.filedownloader.sender.distributor;

import com.khanghoang.filedownloader.sender.core.SocketServerManager;
import com.khanghoang.filedownloader.shared.model.ProtocolChunk;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class FileDistributor {
    private final SocketServerManager socketServerManager;

    public FileDistributor(SocketServerManager socketServerManager) {
        this.socketServerManager = socketServerManager;
    }

    public void distributeTo(List<Socket> clients, byte[] fileData, String fileName) {
        int totalClients = clients.size();
        int chunkSize = fileData.length / totalClients;
        int remainder = fileData.length % totalClients;

        for (int i = 0; i < totalClients; i++) {
            int index = i;
            Socket clientSocket = clients.get(i);

            new Thread(() -> {
                int start = index * chunkSize;
                int end = (index == totalClients - 1) ? fileData.length : start + chunkSize;
                if (index == totalClients - 1) {
                    end += remainder;
                }

                byte[] chunkData = Arrays.copyOfRange(fileData, start, end);

                ProtocolChunk chunk = new ProtocolChunk(
                        fileName,
                        index,
                        totalClients,
                        chunkData.length,
                        chunkData
                );

                byte[] encoded = chunk.encode();

                socketServerManager.send(clientSocket, encoded);
                System.out.println("Sent chunk " + index + " of file [" + fileName + "] to client " + (index + 1));
            }).start();
        }
    }
}