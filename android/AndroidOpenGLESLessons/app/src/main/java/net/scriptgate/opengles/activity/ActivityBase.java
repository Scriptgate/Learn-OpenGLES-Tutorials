package net.scriptgate.opengles.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import net.scriptgate.opengles.renderer.Renderer;

import static net.scriptgate.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToGLSurfaceViewRenderer;
import static net.scriptgate.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToResumable;

public abstract class ActivityBase extends ComponentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);

        if (supportsOpenGLES20()) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(getRendererAdapter());
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        setContentView(glSurfaceView);
        addComponent(adaptToResumable(glSurfaceView));
    }

    public GLSurfaceView.Renderer getRendererAdapter() {
        return adaptToGLSurfaceViewRenderer(getRenderer());
    }

    public abstract Renderer getRenderer();
}
