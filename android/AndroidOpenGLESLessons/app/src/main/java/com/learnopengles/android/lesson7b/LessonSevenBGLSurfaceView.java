package com.learnopengles.android.lesson7b;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class LessonSevenBGLSurfaceView extends GLSurfaceView {

    private VertexBufferObjectRenderer mRenderer;
    // Offsets for touch events
    private float mPreviousX;
    private float mPreviousY;

    private float mDensity;

    public LessonSevenBGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (mRenderer != null) {
                    float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;

                    mRenderer.deltaX += deltaX;
                    mRenderer.deltaY += deltaY;
                }
            }

            mPreviousX = x;
            mPreviousY = y;

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    // Hides superclass method.
    public void setRenderer(VertexBufferObjectRenderer renderer, float density) {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }
}
