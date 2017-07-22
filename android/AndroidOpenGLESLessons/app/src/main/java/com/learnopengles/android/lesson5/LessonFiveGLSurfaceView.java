package com.learnopengles.android.lesson5;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import static com.learnopengles.android.renderer.RendererAdapter.adaptToGLSurfaceViewRenderer;

public class LessonFiveGLSurfaceView extends GLSurfaceView {

    private BlendingRenderer renderer;

    public LessonFiveGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (renderer != null) {
                    // Ensure we call switchMode() on the OpenGL thread.
                    // queueEvent() is a method of GLSurfaceView that will do this for us.
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.switchMode();
                        }
                    });

                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    public void setRenderer(BlendingRenderer renderer) {
        this.renderer = renderer;
        super.setRenderer(adaptToGLSurfaceViewRenderer(renderer));
    }
}
