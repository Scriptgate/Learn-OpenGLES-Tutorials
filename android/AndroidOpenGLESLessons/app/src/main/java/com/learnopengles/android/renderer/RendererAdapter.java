package com.learnopengles.android.renderer;


import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RendererAdapter {

    public static GLSurfaceView.Renderer adaptToGLSurfaceViewRenderer(final Renderer renderer) {
        return new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                renderer.onSurfaceCreated();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                renderer.onSurfaceChanged(width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                renderer.onDrawFrame();
            }
        };
    }
}
