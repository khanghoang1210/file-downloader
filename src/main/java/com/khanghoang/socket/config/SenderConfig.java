package com.khanghoang.socket.config;

import java.util.List;

public class SenderConfig {
    public static final int PORT = 9000;
    public static final int EXPECTED_CLIENTS = 10;
    public static final List<String> FILES = List.of(
            "C:/Users/Ms.Trang/Documents/Progress Test Hoang Khang.docx",
            "C:/Users/Ms.Trang/Documents/hosts.txt"
    );
    public static final int CHUNK_SIZE = 1024;
}
