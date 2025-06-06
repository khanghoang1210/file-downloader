package com.khanghoang.socket.sender;

import com.khanghoang.socket.sender.network.SocketServer;

import java.io.File;

public class SenderApp {
    public static void main(String[] args) {
        SocketServer server = new SocketServer(9000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();

        server.waitForClients(10);
        server.distributeFile(new File("C:\\Users\\Ms.Trang\\Documents\\Progress Test Hoang Khang.docx"));
        server.distributeFile(new File("C:\\Users\\Ms.Trang\\Documents\\hosts.txt"));

    }
}
