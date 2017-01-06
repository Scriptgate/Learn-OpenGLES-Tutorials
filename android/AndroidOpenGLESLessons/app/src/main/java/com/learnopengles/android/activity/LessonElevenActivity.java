package com.learnopengles.android.activity;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.lesson11.LineRenderer;

public class LessonElevenActivity  extends AbstractActivity {
    @Override
    GLSurfaceView.Renderer getRenderer() {
        return new LineRenderer();
    }
}