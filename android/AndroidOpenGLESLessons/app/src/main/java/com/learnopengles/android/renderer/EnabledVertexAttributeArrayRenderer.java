package com.learnopengles.android.renderer;


import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

public class EnabledVertexAttributeArrayRenderer<T> implements  RendererLink<T>{

    private Program program;
    private AttributeVariable attributeVariable;
    private FloatBuffer data;
    private int dataSize;

    public EnabledVertexAttributeArrayRenderer(Program program, AttributeVariable attributeVariable, FloatBuffer data, int dataSize) {
        this.program = program;
        this.attributeVariable = attributeVariable;
        this.data = data;
        this.dataSize = dataSize;
    }

    public void apply(T t) {
        int handle = program.getHandle(attributeVariable);

        data.position(0);
        glVertexAttribPointer(handle, dataSize, GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }

}
