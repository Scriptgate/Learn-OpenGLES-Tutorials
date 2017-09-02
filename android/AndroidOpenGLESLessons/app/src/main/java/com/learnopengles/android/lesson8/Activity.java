package com.learnopengles.android.lesson8;

import android.os.Bundle;
import android.util.DisplayMetrics;

import net.scriptgate.opengles.activity.ComponentActivity;

import static net.scriptgate.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToResumable;


public class Activity extends ComponentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LessonEightGLSurfaceView surfaceView = new LessonEightGLSurfaceView(this);

        if (supportsOpenGLES20()) {
            surfaceView.setEGLContextClientVersion(2);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            surfaceView.setRenderer(new IndexBufferObjectRenderer(this, surfaceView), displayMetrics.density);
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        setContentView(surfaceView);
        addComponent(adaptToResumable(surfaceView));
    }
}