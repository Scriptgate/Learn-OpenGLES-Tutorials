package com.learnopengles.android.common;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

public class BufferHelper {

    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_SHORT = 2;

    public static FloatBuffer allocateFloatBuffer(int numberOfFloats) {
        return allocateDirect(numberOfFloats * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
    }

    public static FloatBuffer allocateBuffer(float[] data) {
        FloatBuffer buffer = allocateFloatBuffer(data.length);
        buffer.put(data).position(0);
        return buffer;
    }

    public static ShortBuffer allocateShortBuffer(int numberOfShorts) {
        return allocateDirect(numberOfShorts * BYTES_PER_SHORT).order(nativeOrder()).asShortBuffer();
    }

    public static ShortBuffer allocateBuffer(short[] data) {
        ShortBuffer buffer = allocateShortBuffer(data.length);
        buffer.put(data).position(0);
        return buffer;
    }

}
