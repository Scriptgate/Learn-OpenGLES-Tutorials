package com.learnopengles.android.lesson4;


import com.learnopengles.android.lesson2.CubeData;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;

public class TextureCubeData {

    private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;

    private final CubeData cubeData;
    private final FloatBuffer cubeTextureCoordinates;

    public TextureCubeData() {

        cubeData = new CubeData();
        final float[] cubeTextureCoordinateData = generateTextureData();
        cubeTextureCoordinates = allocateBuffer(cubeTextureCoordinateData);
    }

    public void passPositionTo(int handle) {
        cubeData.passPositionTo(handle);
    }

    public void passColorTo(int handle) {
        cubeData.passColorTo(handle);
    }

    public void passNormalTo(int handle) {
        cubeData.passNormalTo(handle);
    }

    public void passTextureTo(int handle) {
        cubeTextureCoordinates.position(0);
        glVertexAttribPointer(handle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, cubeTextureCoordinates);
        glEnableVertexAttribArray(handle);
    }
}
