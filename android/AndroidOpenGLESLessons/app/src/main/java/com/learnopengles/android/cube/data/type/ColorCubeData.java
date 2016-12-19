package com.learnopengles.android.cube.data.type;


import com.learnopengles.android.cube.data.CubeData;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class ColorCubeData implements CubeData {

    private static final int COLOR_DATA_SIZE = 4;

    private final FloatBuffer colors;

    public ColorCubeData(float[] colorData) {
        colors = allocateBuffer(colorData);
    }

    @Override
    public void passTo(int handle) {
        colors.position(0);
        glVertexAttribPointer(handle, COLOR_DATA_SIZE, GL_FLOAT, false, 0, colors);
        glEnableVertexAttribArray(handle);
    }
}
