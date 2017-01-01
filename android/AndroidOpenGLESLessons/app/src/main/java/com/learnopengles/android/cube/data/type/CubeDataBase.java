package com.learnopengles.android.cube.data.type;


import com.learnopengles.android.cube.data.CubeData;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class CubeDataBase implements CubeData {

    private final int dataSize;
    private final FloatBuffer data;

    protected CubeDataBase(float[] data, int dataSize) {
        this.data = allocateBuffer(data);
        this.dataSize = dataSize;
    }

    @Override
    public void passTo(int handle) {
        data.position(0);
        glVertexAttribPointer(handle, dataSize, GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }

}
