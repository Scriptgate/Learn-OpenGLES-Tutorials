package com.learnopengles.android.cube.data.type;


import com.learnopengles.android.cube.data.CubeData;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class NormalCubeData implements CubeData {

    private static final int NORMAL_DATA_SIZE = 3;

    private final FloatBuffer normals;

    public NormalCubeData(float[] normalData) {
        normals = allocateBuffer(normalData);
    }

    @Override
    public void passTo(int handle) {
        normals.position(0);
        glVertexAttribPointer(handle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, normals);
        glEnableVertexAttribArray(handle);
    }
}
