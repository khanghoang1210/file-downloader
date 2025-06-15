package com.khanghoang.socket.shared.interfaces;

import java.io.IOException;

public interface FileWriter {
    void writeChunk(String fileName, int chunkIndex, byte[] data) throws IOException;
    void assembleFile(String fileName, int totalChunks) throws IOException;
    String getOutputDir();
    String getTempDir();
} 