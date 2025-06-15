package com.khanghoang.socket.shared.interfaces;

import com.khanghoang.socket.shared.model.ProtocolChunk;
import java.io.IOException;

public interface ProtocolHandler {
    void encodeChunk(ProtocolChunk chunk) throws IOException;
    ProtocolChunk decodeChunk() throws IOException;
} 