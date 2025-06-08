package com.khanghoang.socket.receiver.helper;

import com.khanghoang.socket.config.ReceiverConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class FileAssembler {
    public static void assembleFile(String fileName, int totalChunks, String tempDir, String outputDir) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        File outputFile = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (int i = 0; i < totalChunks; i++) {
                File partFile = new File(tempDir + fileName + ".part" + i);
                if (!partFile.exists()) {
                    throw new IOException("Missing chunk file: " + partFile.getName());
                }

                try (FileInputStream fis = new FileInputStream(partFile)) {
                    byte[] buffer = new byte[ReceiverConfig.BUFFER_SIZE]; // create buffer to write files to disk
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                    }
                }
                // remove temp files after assemble
                if (!partFile.delete()) {
                    System.err.println("Warning: Could not delete temp file " + partFile.getName());
                }
            }
        }

        System.out.println("File assembled at: " + outputFile.getAbsolutePath());
    }
}
