package com.learnopengles.android.cube;

import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

public abstract class CubeRendererBase {

    protected final Program program;

    public CubeRendererBase(Program program) {
        this.program = program;
    }

    protected void passDataToAttribute(Cube cube, AttributeVariable attributeVariable) {
        int handle = program.getHandle(attributeVariable);

        FloatBuffer data = cube.getData(attributeVariable);
        data.position(0);
        glVertexAttribPointer(handle, attributeVariable.getSize(), GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }

}
