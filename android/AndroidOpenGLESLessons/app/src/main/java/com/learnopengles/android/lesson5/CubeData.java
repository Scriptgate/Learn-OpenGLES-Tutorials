package com.learnopengles.android.lesson5;

import com.learnopengles.android.cube.CubeDataFactory;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.Color.BLACK;
import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.CYAN;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.MAGENTA;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.Color.WHITE;
import static com.learnopengles.android.common.Color.YELLOW;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class CubeData {

    private static final int POSITION_DATA_SIZE = 3;
    private static final int COLOR_DATA_SIZE = 4;

    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer cubePositions;
    private final FloatBuffer cubeColors;

    public CubeData() {
        final float[] cubePositionData = CubeDataFactory.generatePositionData(1.0f, 1.0f, 1.0f);

        final float[] cubeColorData = CubeDataFactory.generateColorData(RED, MAGENTA, BLACK, BLUE, YELLOW, WHITE, GREEN, CYAN);

        // Initialize the buffers.
        cubePositions = allocateBuffer(cubePositionData);
        cubeColors = allocateBuffer(cubeColorData);
    }

    public void passPositionTo(int handle) {
        cubePositions.position(0);
        glVertexAttribPointer(handle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, cubePositions);
        glEnableVertexAttribArray(handle);
    }

    public void passColorTo(int handle) {
        cubeColors.position(0);
        glVertexAttribPointer(handle, COLOR_DATA_SIZE, GL_FLOAT, false, 0, cubeColors);
        glEnableVertexAttribArray(handle);
    }
}
