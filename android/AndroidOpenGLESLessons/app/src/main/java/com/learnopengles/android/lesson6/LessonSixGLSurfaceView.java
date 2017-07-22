package com.learnopengles.android.lesson6;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import static com.learnopengles.android.renderer.RendererAdapter.adaptToGLSurfaceViewRenderer;

public class LessonSixGLSurfaceView extends GLSurfaceView {
    private TextureFilteringRenderer mRenderer;

    // Offsets for touch events
    private float mPreviousX;
    private float mPreviousY;

    private float mDensity;

    public LessonSixGLSurfaceView(Context context) {
        super(context);
    }

    public LessonSixGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public void setRenderer(TextureFilteringRenderer renderer, float density) {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(adaptToGLSurfaceViewRenderer(renderer));
    }
}
