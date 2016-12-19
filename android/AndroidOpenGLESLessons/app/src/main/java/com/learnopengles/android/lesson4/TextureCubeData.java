package com.learnopengles.android.lesson4;


import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.CYAN;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.MAGENTA;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.Color.YELLOW;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;

public class TextureCubeData {

    private static final int POSITION_DATA_SIZE = 3;
    private static final int COLOR_DATA_SIZE = 4;
    private static final int NORMAL_DATA_SIZE = 3;
    private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;

    private final FloatBuffer cubePositions;
    private final FloatBuffer cubeColors;
    private final FloatBuffer cubeNormals;
    private final FloatBuffer cubeTextureCoordinates;

    public TextureCubeData() {
        // Define points for a cube.
        final float[] cubePositionData = generatePositionData(1.0f, 1.0f, 1.0f);
        final float[] cubeColorData = generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA);
        final float[] cubeNormalData = generateNormalData();
        final float[] cubeTextureCoordinateData = generateTextureData();

        // Initialize the buffers.
        cubePositions = allocateBuffer(cubePositionData);
        cubeColors = allocateBuffer(cubeColorData);
        cubeNormals = allocateBuffer(cubeNormalData);
        cubeTextureCoordinates = allocateBuffer(cubeTextureCoordinateData);
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

    public void passNormalTo(int handle) {
        cubeNormals.position(0);
        glVertexAttribPointer(handle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, cubeNormals);
        glEnableVertexAttribArray(handle);
    }

    public void passTextureTo(int handle) {
        cubeTextureCoordinates.position(0);
        glVertexAttribPointer(handle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, cubeTextureCoordinates);
        glEnableVertexAttribArray(handle);
    }
}