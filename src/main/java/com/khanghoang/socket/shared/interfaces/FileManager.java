package com.khanghoang.socket.shared.interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface FileManager {
    void write(String outputDir,byte[] data);
    byte[] read(String filePath);
    void remove(String filePath);
    InputStream readStream(String path) throws IOException;

}
