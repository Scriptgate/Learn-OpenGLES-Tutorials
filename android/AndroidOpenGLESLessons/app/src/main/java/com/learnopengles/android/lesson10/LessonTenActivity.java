package com.learnopengles.android.lesson10;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.AbstractActivity;

public class LessonTenActivity extends AbstractActivity {
    @Override
    public Renderer getRenderer() {
        return new AdvancedTexturingRenderer(this);
    }
}