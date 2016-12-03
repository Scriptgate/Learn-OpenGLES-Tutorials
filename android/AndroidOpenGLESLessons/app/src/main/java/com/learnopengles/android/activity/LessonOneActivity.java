package com.learnopengles.android.activity;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.lesson1.BasicDrawingRenderer;

public class LessonOneActivity extends AbstractActivity {

    @Override
    GLSurfaceView.Renderer getRenderer() {
        return new BasicDrawingRenderer();
    }
}