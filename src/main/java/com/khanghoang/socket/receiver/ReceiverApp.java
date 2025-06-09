package com.khanghoang.socket.receiver;


import com.khanghoang.socket.config.AppConfig;
import com.khanghoang.socket.receiver.core.Receiver;

public class ReceiverApp {
    public static void main(String[] args) {
        Receiver receiver = new Receiver(AppConfig.NUM_CLIENTS);
        receiver.run();
    }
}
