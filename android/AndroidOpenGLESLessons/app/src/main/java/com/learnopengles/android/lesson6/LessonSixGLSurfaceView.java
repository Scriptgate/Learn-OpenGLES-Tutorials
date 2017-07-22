package com.learnopengles.android.lesson6;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import static com.learnopengles.android.renderer.RendererAdapter.adaptToGLSurfaceViewRenderer;

public class LessonSixGLSurfaceView extends GLSurfaceView {

    private TextureFilteringRenderer renderer;

    private float previousX;
    private float previousY;

    private float density;

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

    public void setRenderer(TextureFilteringRenderer renderer, float density) {
        this.renderer = renderer;
        this.density = density;
        super.setRenderer(adaptToGLSurfaceViewRenderer(renderer));
    }
}
