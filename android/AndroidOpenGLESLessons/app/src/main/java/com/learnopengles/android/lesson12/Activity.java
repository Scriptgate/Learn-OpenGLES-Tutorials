package com.learnopengles.android.lesson12;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import net.scriptgate.android.opengles.activity.ComponentActivity;

import static net.scriptgate.android.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToGLSurfaceViewRenderer;
import static net.scriptgate.android.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToResumable;

public class Activity extends ComponentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DankMemesRenderer renderer = new DankMemesRenderer(this);
        GLSurfaceView view = new GLSurfaceView(this);

        if (supportsOpenGLES20()) {
            view.setEGLContextClientVersion(2);
            view.setRenderer(adaptToGLSurfaceViewRenderer(renderer));
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        setContentView(view);
        addComponent(adaptToResumable(view));

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        addComponent(new SensorService(sensorManager, renderer));
    }

}