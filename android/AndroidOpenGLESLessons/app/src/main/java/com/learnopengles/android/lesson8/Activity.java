package com.learnopengles.android.lesson8;

import android.os.Bundle;
import android.util.DisplayMetrics;

import net.scriptgate.opengles.activity.ActivityWithViewBase;

public class Activity extends ActivityWithViewBase<LessonEightGLSurfaceView> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new LessonEightGLSurfaceView(this);

        if (supportsOpenGLES20()) {
            glSurfaceView.setEGLContextClientVersion(2);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            glSurfaceView.setRenderer(new IndexBufferObjectRenderer(this, glSurfaceView), displayMetrics.density);
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        setContentView(glSurfaceView);
    }
}