package com.learnopengles.android.lesson7;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.scriptgate.opengles.renderer.RendererAdapter;

public class LessonSevenGLSurfaceView extends GLSurfaceView {

    private VertexBufferObjectRenderer renderer;

    private float previousX;
    private float previousY;

    private float density;

    public LessonSevenGLSurfaceView(Context context) {
        super(context);
    }

    public LessonSevenGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (renderer != null) {
                    float deltaX = (x - previousX) / density / 2f;
                    float deltaY = (y - previousY) / density / 2f;

                    renderer.deltaX += deltaX;
                    renderer.deltaY += deltaY;
                }
            }

            previousX = x;
            previousY = y;

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    public void setRenderer(VertexBufferObjectRenderer renderer, float density) {
        this.renderer = renderer;
        this.density = density;
        super.setRenderer(RendererAdapter.adaptToGLSurfaceViewRenderer(renderer));
    }
}
