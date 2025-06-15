package com.khanghoang.socket.shared.interfaces;

import java.io.File;
import java.io.IOException;

public interface FileHandler {
    void handleFile(File file) throws IOException;
    void saveChunk(String fileName, int chunkIndex, byte[] data) throws IOException;
    void assembleFile(String fileName, int totalChunks) throws IOException;
    String getOutputDir();
    String getTempDir();
} 