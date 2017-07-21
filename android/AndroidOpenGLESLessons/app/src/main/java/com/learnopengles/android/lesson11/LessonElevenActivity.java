package com.learnopengles.android.lesson11;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.AbstractActivity;

public class LessonElevenActivity  extends AbstractActivity {
    @Override
    public Renderer getRenderer() {
        return new LineRenderer();
    }
}