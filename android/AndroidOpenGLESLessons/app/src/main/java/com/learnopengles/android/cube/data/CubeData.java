package com.learnopengles.android.cube.data;


import java.nio.FloatBuffer;

import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class CubeData {

    private final int dataSize;
    private final FloatBuffer data;

    public CubeData(float[] data, int dataSize) {
        this.data = allocateBuffer(data);
        this.dataSize = dataSize;
    }

    public int getDataSize() {
        return dataSize;
    }

    public FloatBuffer getData() {
        return data;
    }

}
