package com.learnopengles.android.lesson4;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.AbstractActivity;

public class LessonFourActivity extends AbstractActivity {
    @Override
    public Renderer getRenderer() {
        return new BasicTexturingRenderer(this);
    }
}