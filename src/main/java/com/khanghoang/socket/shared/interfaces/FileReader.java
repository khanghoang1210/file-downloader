package com.khanghoang.socket.shared.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public interface FileReader {
    void readFile(File file, Consumer<byte[]> chunkConsumer) throws IOException;
    String getOutputDir();
} 