package com.learnopengles.android.renderer;


import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttrib3fv;

public class VertexAttrib3fvRenderer<T> implements RendererLink<T> {

    private final Program program;
    private final AttributeVariable attributeVariable;
    private final float[] data;

    public VertexAttrib3fvRenderer(Program program, AttributeVariable attributeVariable, float[] data) {
        this.program = program;
        this.attributeVariable = attributeVariable;
        this.data = data;
    }

    @Override
    public void apply(T t) {
        int handle = program.getHandle(attributeVariable);
        glDisableVertexAttribArray(handle);
        glVertexAttrib3fv(handle, data, 0);
    }
}
