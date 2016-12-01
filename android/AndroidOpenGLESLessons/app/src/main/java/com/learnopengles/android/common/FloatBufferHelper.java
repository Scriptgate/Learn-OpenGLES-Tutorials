package com.learnopengles.android.common;

import java.nio.FloatBuffer;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

public class FloatBufferHelper {

    public static final int BYTES_PER_FLOAT = 4;

    public static FloatBuffer allocateBuffer(float[] data) {
        FloatBuffer buffer = allocateDirect(data.length * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

}
