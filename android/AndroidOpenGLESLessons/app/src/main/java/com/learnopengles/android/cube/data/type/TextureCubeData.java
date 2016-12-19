package com.learnopengles.android.cube.data.type;

import com.learnopengles.android.cube.data.CubeData;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class TextureCubeData implements CubeData {

    private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;

    private final FloatBuffer textureCoordinates;

    public TextureCubeData(float[] textureCoordinateData) {
        textureCoordinates = allocateBuffer(textureCoordinateData);
    }

    @Override
    public void passTo(int handle) {
        textureCoordinates.position(0);
        glVertexAttribPointer(handle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, textureCoordinates);
        glEnableVertexAttribArray(handle);
    }
}
