package com.khanghoang.socket.receiver;


import com.khanghoang.socket.config.ReceiverConfig;
import com.khanghoang.socket.receiver.solution.MultiReceiver;
import com.khanghoang.socket.receiver.solution.Receiver;

public class ReceiverApp {
    public static void main(String[] args) {
        Receiver receiver = new MultiReceiver(ReceiverConfig.NUM_CLIENTS);
        receiver.run();
    }
}
