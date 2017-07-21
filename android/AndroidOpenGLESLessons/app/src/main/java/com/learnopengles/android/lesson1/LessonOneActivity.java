package com.learnopengles.android.lesson1;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.AbstractActivity;

public class LessonOneActivity extends AbstractActivity {

    @Override
    public Renderer getRenderer() {
        return new BasicDrawingRenderer();
    }
}