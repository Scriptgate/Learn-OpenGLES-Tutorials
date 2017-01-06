package com.learnopengles.android.renderer.circle;


import com.learnopengles.android.lesson11.Circle;
import com.learnopengles.android.renderer.DrawArraysRenderer;

import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static com.learnopengles.android.lesson11.Circle.NUMBER_OF_POINTS;

public class DrawCircleRendererFactory {

    public static DrawArraysRenderer<Circle> fillCircleRenderer() {
        return new DrawArraysRenderer<>(GL_TRIANGLE_FAN, NUMBER_OF_POINTS);
    }

    public static DrawArraysRenderer<Circle> traceCircleRenderer() {
        return new DrawArraysRenderer<>(GL_LINE_LOOP, NUMBER_OF_POINTS);
    }

}
