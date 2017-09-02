package net.scriptgate.opengles.activity.adapter;

import android.opengl.GLSurfaceView;

import net.scriptgate.opengles.activity.Resumable;
import net.scriptgate.opengles.renderer.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewAdapter {

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

    public static Resumable adaptToResumable(final GLSurfaceView glSurfaceView) {
        return new Resumable() {

            @Override
            public void onResume() {
                glSurfaceView.onResume();
            }

            @Override
            public void onPause() {
                glSurfaceView.onPause();
            }
        };
    }


}
