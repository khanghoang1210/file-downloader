package com.khanghoang.filedownloader.receiver;


import com.khanghoang.filedownloader.receiver.solution.BaseReceiverSolution;
import com.khanghoang.filedownloader.receiver.solution.Solution;

public class Main {
    public static void main(String[] args) {
        Solution solution = new BaseReceiverSolution();
        solution.solve();
    }
}
