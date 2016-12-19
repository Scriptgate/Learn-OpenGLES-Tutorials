package com.learnopengles.android.cube.data.type;


import com.learnopengles.android.cube.data.CubeData;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class PositionCubeData implements CubeData {

    private static final int POSITION_DATA_SIZE = 3;

    private final FloatBuffer positions;

    public PositionCubeData(float[] positionData) {
        positions = allocateBuffer(positionData);
    }

    @Override
    public void passTo(int handle) {
        positions.position(0);
        glVertexAttribPointer(handle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, positions);
        glEnableVertexAttribArray(handle);
    }
}
