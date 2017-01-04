package com.learnopengles.android.renderer;


import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

public abstract class VertexAttribPointerRenderer<T> implements RendererLink<T>{

    protected void apply(int handle, FloatBuffer data, int dataSize) {
        data.position(0);
        glVertexAttribPointer(handle, dataSize, GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }

}
