package com.learnopengles.android.lesson8;

import android.os.Bundle;
import android.util.DisplayMetrics;

import com.learnopengles.android.activity.ActivityWithViewBase;

public class LessonEightActivity extends ActivityWithViewBase<LessonEightGLSurfaceView> {

    private IndexBufferObjectRenderer renderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new LessonEightGLSurfaceView(this);

        setContentView(glSurfaceView);

        if (supportsOpenGLES20()) {

            glSurfaceView.setEGLContextClientVersion(2);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            renderer = new IndexBufferObjectRenderer(this, glSurfaceView);
            glSurfaceView.setRenderer(renderer, displayMetrics.density);
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }
    }
}