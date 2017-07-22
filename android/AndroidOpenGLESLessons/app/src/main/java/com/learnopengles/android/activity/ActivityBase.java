package com.learnopengles.android.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.learnopengles.android.renderer.Renderer;

import static com.learnopengles.android.renderer.RendererAdapter.adaptToGLSurfaceViewRenderer;

public abstract class ActivityBase extends ActivityWithViewBase<GLSurfaceView> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);

        if (supportsOpenGLES20()) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(getRendererAdapter());
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        setContentView(glSurfaceView);
    }

    public GLSurfaceView.Renderer getRendererAdapter() {
        return adaptToGLSurfaceViewRenderer(getRenderer());
    }

    public abstract Renderer getRenderer();
}
