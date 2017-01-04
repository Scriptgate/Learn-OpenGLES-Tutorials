package com.learnopengles.android.renderer;


import com.learnopengles.android.program.Program;

public interface RendererLink<T> {

    void apply(Program program, T t);

}
