package com.khanghoang.filedownloader.receiver.assembler;

import com.khanghoang.filedownloader.config.AppConfig;
import com.khanghoang.filedownloader.shared.interfaces.FileManager;
import com.khanghoang.filedownloader.shared.model.ProtocolChunk;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class FileAssembler {
    private final String outputDir;
    private final String tempDir;
    private final FileManager fileManager;
    private final Set<String> mergedFiles = ConcurrentHashMap.newKeySet();

    public FileAssembler(FileManager fileManager, String outputDir) {
        this.fileManager = fileManager;
        this.outputDir = outputDir;
        this.tempDir = outputDir + "temp";
        new File(outputDir).mkdirs();
        new File(tempDir).mkdirs(); // ensure folder exists
    }

    public synchronized void storeChunk(ProtocolChunk chunk) throws IOException {
        String fileName = chunk.getFileName();
        int chunkIndex = chunk.getChunkIndex();
        byte[] data = chunk.getData();

        String chunkPath = tempDir + "/" + fileName + "." + chunkIndex + ".chunk";
        fileManager.write(chunkPath, data);

        System.out.println("Stored chunk " + chunkIndex + " of " + fileName);

        // check if all chunks exist
        if (!mergedFiles.contains(fileName) && isComplete(fileName, chunk.getTotalChunks())) {
            synchronized (mergedFiles) {
                if (mergedFiles.add(fileName)) {
                    mergeChunks(fileName, chunk.getTotalChunks());
                }
            }
        }
    }

    private boolean isComplete(String fileName, int totalChunks) {
        for (int i = 0; i < totalChunks; i++) {
            File chunk = new File(tempDir + "/" + fileName + "." + i + ".chunk");
            if (!chunk.exists()) return false;
        }
        return true;
    }

    private void mergeChunks(String fileName, int totalChunks) throws IOException {
        String outputPath = outputDir + fileName;
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath))) {
            byte[] buffer = new byte[AppConfig.BUFFER_SIZE]; // 8KB buffer

            for (int i = 0; i < totalChunks; i++) {
                String chunkPath = tempDir + "/" + fileName + "." + i + ".chunk";
                try (var in = fileManager.readStream(chunkPath)) {
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            }
            out.flush();
        }

        System.out.println("Merged all chunks into file: " + outputPath);

        //remove tmp file after merge chunk
        for (int i = 0; i < totalChunks; i++) {
            String chunkPath = tempDir + "/" + fileName + "." + i + ".chunk";
            fileManager.remove(chunkPath);
            System.out.println("Removed temp chunk: " + chunkPath);
        }
    }
}
