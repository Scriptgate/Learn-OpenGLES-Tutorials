package com.learnopengles.android.lesson7b;

import android.content.Context;
import android.opengl.GLSurfaceView;

import static net.scriptgate.android.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToGLSurfaceViewRenderer;

public class LessonSevenBGLSurfaceView extends GLSurfaceView {

    public LessonSevenBGLSurfaceView(Context context) {
        super(context);
    }

    public void setRenderer(VertexBufferObjectRenderer renderer) {
        super.setRenderer(adaptToGLSurfaceViewRenderer(renderer));
    }
}
