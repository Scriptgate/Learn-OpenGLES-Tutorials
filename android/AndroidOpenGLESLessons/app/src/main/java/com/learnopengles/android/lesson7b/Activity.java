package com.learnopengles.android.lesson7b;

import android.os.Bundle;

import com.learnopengles.android.activity.ActivityWithViewBase;

public class Activity extends ActivityWithViewBase<LessonSevenBGLSurfaceView> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new LessonSevenBGLSurfaceView(this);

        if (supportsOpenGLES20()) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new VertexBufferObjectRenderer(this, glSurfaceView));
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        setContentView(glSurfaceView);
    }
}