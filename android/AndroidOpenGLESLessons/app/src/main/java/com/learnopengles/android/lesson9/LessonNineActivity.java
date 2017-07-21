package com.learnopengles.android.lesson9;


import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.AbstractActivity;

public class LessonNineActivity extends AbstractActivity {
    @Override
    public Renderer getRenderer() {
        return new CameraRenderer(this);
    }
}
