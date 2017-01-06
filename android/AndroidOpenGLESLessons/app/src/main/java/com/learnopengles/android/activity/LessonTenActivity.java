package com.learnopengles.android.activity;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.lesson10.AdvancedTexturingRenderer;

public class LessonTenActivity extends AbstractActivity {
    @Override
    GLSurfaceView.Renderer getRenderer() {
        return new AdvancedTexturingRenderer(this);
    }
}