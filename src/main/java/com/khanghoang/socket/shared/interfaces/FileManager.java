package com.khanghoang.socket.shared.interfaces;

public interface FileManager {
    void write(String outputDir,byte[] data);
    byte[] read(String filePath);
    void remove(String filePath);
}
