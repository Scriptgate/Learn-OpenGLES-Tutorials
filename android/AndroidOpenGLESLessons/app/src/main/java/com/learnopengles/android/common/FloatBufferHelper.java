package com.learnopengles.android.common;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

public class FloatBufferHelper {

    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_SHORT = 2;

    public static FloatBuffer allocateBuffer(float[] data) {
        FloatBuffer buffer = allocateDirect(data.length * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

    public static ShortBuffer allocateBuffer(short[] data) {
        ShortBuffer buffer = allocateDirect(data.length * BYTES_PER_SHORT).order(nativeOrder()).asShortBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

}
