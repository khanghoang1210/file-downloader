package com.khanghoang.socket.receiver.assembler;

import com.khanghoang.socket.shared.interfaces.FileManager;
import com.khanghoang.socket.shared.model.ProtocolChunk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileAssembler {
    private final String outputDir;
    private final String tempDir;
    private final FileManager fileManager;

    public FileAssembler(FileManager fileManager, String outputDir) {
        this.fileManager = fileManager;
        this.outputDir = outputDir;
        this.tempDir = outputDir + "temp";
        new File(outputDir).mkdirs();
        new File(tempDir).mkdirs(); // ensure folder exists
    }

    public void storeChunk(ProtocolChunk chunk) throws IOException {
        String fileName = chunk.getFileName();
        int chunkIndex = chunk.getChunkIndex();
        byte[] data = chunk.getData();

        String chunkPath = tempDir + "/" + fileName + "." + chunkIndex + ".chunk";
        fileManager.write(chunkPath, data);

        System.out.println("Stored chunk " + chunkIndex + " of " + fileName);

        // check if all chunks exist
        if (isComplete(fileName, chunk.getTotalChunks())) {
            mergeChunks(fileName, chunk.getTotalChunks());
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
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            for (int i = 0; i < totalChunks; i++) {
                String chunkPath = tempDir + "/" + fileName + "." + i + ".chunk";
                byte[] chunkData = fileManager.read(chunkPath);
                out.write(chunkData);
            }
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
