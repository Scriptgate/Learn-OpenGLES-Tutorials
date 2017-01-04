package com.learnopengles.android.renderer;


import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.glDrawArrays;

public class DrawArraysRenderer<T> implements RendererLink<T> {

    private int mode;
    private int count;

    public DrawArraysRenderer(int mode, int count) {
        this.mode = mode;
        this.count = count;
    }

    @Override
    public void apply(Program program, T t) {
        glDrawArrays(mode, 0, count);
    }
}
