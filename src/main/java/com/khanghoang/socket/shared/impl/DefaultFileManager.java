package com.khanghoang.socket.shared.impl;

import com.khanghoang.socket.shared.interfaces.FileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DefaultFileManager implements FileManager {
    @Override
    public void write(String outputDir, byte[] data) {
        try {
            Files.write(Paths.get(outputDir), data);
        } catch (IOException e) {
            System.err.println("Failed to write file: " + outputDir);
            e.printStackTrace();
        }
    }

    @Override
    public byte[] read(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Failed to read file: " + filePath);
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public void remove(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filePath);
            e.printStackTrace();
        }
    }
}
