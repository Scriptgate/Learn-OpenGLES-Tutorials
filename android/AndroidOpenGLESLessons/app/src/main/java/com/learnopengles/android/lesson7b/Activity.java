package com.learnopengles.android.lesson7b;

import android.os.Bundle;

import net.scriptgate.android.opengles.activity.ComponentActivity;

import static net.scriptgate.android.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToResumable;


public class Activity extends ComponentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LessonSevenBGLSurfaceView surfaceView = new LessonSevenBGLSurfaceView(this);

        if (supportsOpenGLES20()) {
            surfaceView.setEGLContextClientVersion(2);
            surfaceView.setRenderer(new VertexBufferObjectRenderer(this, surfaceView));
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        setContentView(surfaceView);
        addComponent(adaptToResumable(surfaceView));
    }
}