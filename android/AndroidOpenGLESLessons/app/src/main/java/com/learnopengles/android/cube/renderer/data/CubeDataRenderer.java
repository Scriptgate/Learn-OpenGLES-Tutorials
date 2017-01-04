package com.learnopengles.android.cube.renderer.data;


import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeData;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.cube.renderer.CubeRenderer;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

public abstract class CubeDataRenderer implements CubeRenderer {

    protected void apply(Cube cube, CubeDataType cubeDataType, int handle) {
        CubeData cubeData = cube.getCubeData(cubeDataType);
        FloatBuffer data = cubeData.getData();
        int dataSize = cubeData.getDataSize();

        data.position(0);
        glVertexAttribPointer(handle, dataSize, GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }

}
