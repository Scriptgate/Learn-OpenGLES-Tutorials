package com.learnopengles.android.renderer.drawable;


import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.VertexAttribPointerRenderer;

import java.nio.FloatBuffer;

public class DrawablePositionRenderer<T extends Drawable> extends VertexAttribPointerRenderer<T> {

    private static final int VERTEX_DATA_SIZE = 3;

    @Override
    public void apply(Program program, T drawable) {
        int handle = program.getHandle(AttributeVariable.POSITION);
        FloatBuffer data = drawable.getPositionData();
        apply(handle, data, VERTEX_DATA_SIZE);
    }
}
