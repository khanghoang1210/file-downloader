package com.khanghoang.socket.receiver.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class FileAssemblerService {

    public static void assembleFile(String fileName, int totalChunks, Map<Integer, byte[]> chunkMap, String outputDir) throws IOException {
        if (chunkMap.size() != totalChunks) {
            throw new IllegalStateException("Missing chunks: expected " + totalChunks + ", got " + chunkMap.size());
        }

        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        File outputFile = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (int i = 0; i < totalChunks; i++) {
                byte[] chunk = chunkMap.get(i);
                if (chunk == null) {
                    throw new IOException("Missing chunk index: " + i);
                }
                fos.write(chunk);
            }
        }

        System.out.println("File assembled at: " + outputFile.getAbsolutePath());
    }
}
