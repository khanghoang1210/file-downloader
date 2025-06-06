package com.khanghoang.socket.receiver;


import com.khanghoang.socket.receiver.solution.MultiReceiver;
import com.khanghoang.socket.receiver.solution.Receiver;

public class ReceiverApp {
    public static void main(String[] args) {
        Receiver receiver = new MultiReceiver();
        receiver.run();
    }
}
