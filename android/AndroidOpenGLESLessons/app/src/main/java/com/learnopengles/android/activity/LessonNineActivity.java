package com.learnopengles.android.activity;


import android.opengl.GLSurfaceView;

import com.learnopengles.android.lesson9.CameraRenderer;

public class LessonNineActivity extends AbstractActivity {
    @Override
    GLSurfaceView.Renderer getRenderer() {
        return new CameraRenderer();
    }
}
