package com.learnopengles.android.renderer.drawable;


import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttrib4fv;

public class DrawableColorRenderer<T extends Drawable> implements RendererLink<T> {

    @Override
    public void apply(Program program, T drawable) {
        int handle = program.getHandle(AttributeVariable.COLOR);
        glDisableVertexAttribArray(handle);
        glVertexAttrib4fv(handle, drawable.getColor(), 0);
    }
}
