package com.learnopengles.android.renderer.drawable;


import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttrib4fv;

public class DrawableColorRenderer<T extends Drawable> implements RendererLink<T> {

    private final Program program;

    public DrawableColorRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(T drawable) {
        int handle = program.getHandle(AttributeVariable.COLOR);
        glDisableVertexAttribArray(handle);
        glVertexAttrib4fv(handle, drawable.getColor(), 0);
    }
}
