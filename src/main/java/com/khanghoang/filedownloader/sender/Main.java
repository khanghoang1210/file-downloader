package com.khanghoang.filedownloader.sender;

import com.khanghoang.filedownloader.sender.solution.BaseSenderSolution;
import com.khanghoang.filedownloader.sender.solution.Solution;

public class Main {
    public static void main(String[] args) {
       Solution solution = new BaseSenderSolution();
       solution.solve();
    }
}
