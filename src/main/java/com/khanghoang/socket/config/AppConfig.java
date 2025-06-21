package com.khanghoang.socket.config;

import java.util.List;

public class AppConfig {
    public static final int BUFFER_SIZE = 1024;
    public static final int NUM_CLIENTS = 10;
    public static final String OUT_DIR = "/Users/anfin/Downloads/test/";
    public static final int PORT = 9000;
    public static final int EXPECTED_CLIENTS = 10;
    public static final List<String> FILES = List.of(
            "/Users/anfin/Downloads/MXV API document/MXVAPI_Guide_v1.3.0.pdf",
            "/Users/anfin/Downloads/MXV API document/MXVAPI_Huong_dan_Test_UAT_v1.0.pdf"
    );
    public static final int CHUNK_SIZE = 1024;
}
