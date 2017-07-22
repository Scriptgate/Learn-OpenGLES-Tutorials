package com.learnopengles.android.lesson9;


import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.ActivityBase;

public class Activity extends ActivityBase {
    @Override
    public Renderer getRenderer() {
        return new CameraRenderer(this);
    }
}
