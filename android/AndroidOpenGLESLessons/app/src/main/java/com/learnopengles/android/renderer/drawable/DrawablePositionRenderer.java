package com.learnopengles.android.renderer.drawable;


import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

public class DrawablePositionRenderer implements RendererLink<Drawable> {

    private static final int VERTEX_DATA_SIZE  =3;

    private Program program;

    public DrawablePositionRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Drawable drawable) {
        int handle = program.getHandle(AttributeVariable.POSITION);

        FloatBuffer data = drawable.getPositionData();
        data.position(0);
        glVertexAttribPointer(handle, VERTEX_DATA_SIZE, GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }
}
