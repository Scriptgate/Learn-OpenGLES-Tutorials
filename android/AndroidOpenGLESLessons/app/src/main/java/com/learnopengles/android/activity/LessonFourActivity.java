package com.learnopengles.android.activity;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.lesson4.BasicTexturingRenderer;

public class LessonFourActivity extends AbstractActivity {
    @Override
    GLSurfaceView.Renderer getRenderer() {
        return new BasicTexturingRenderer(this);
    }
}