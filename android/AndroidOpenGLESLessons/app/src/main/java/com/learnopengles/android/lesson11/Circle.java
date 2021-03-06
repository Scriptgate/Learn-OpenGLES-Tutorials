package com.learnopengles.android.lesson11;


import net.scriptgate.android.common.Color;
import net.scriptgate.android.common.Point3D;

import java.nio.FloatBuffer;

import static net.scriptgate.android.nio.BufferHelper.allocateBuffer;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

class Circle implements Drawable {

    static final int NUMBER_OF_POINTS = 360;

    private FloatBuffer vertexBuffer;

    private static final int VERTEX_DATA_SIZE = 3;

    private static final float DEG2RAD = 3.14159f / 180;

    private Color color;

    private Circle(Color color, float[] vertices) {
        this.color = color;
        this.vertexBuffer = allocateBuffer(vertices);
    }

    static Circle createCircleInXPlane(Color color, Point3D center, float radius) {
        float[] vertices = new float[NUMBER_OF_POINTS * VERTEX_DATA_SIZE];

        int index = 0;
        float increment = 360.0f / NUMBER_OF_POINTS;
        for (float angle = 0; angle < 360; angle += increment) {
            float degInRad = angle * DEG2RAD;
            vertices[index++] = center.x();
            vertices[index++] = (float) (center.y() + radius * cos(degInRad));
            vertices[index++] = (float) (center.z() + radius * sin(degInRad));
        }
        return new Circle(color, vertices);
    }

    static Circle createCircleInYPlane(Color color, Point3D center, float radius) {
        float[] vertices = new float[NUMBER_OF_POINTS * VERTEX_DATA_SIZE];

        int index = 0;
        float increment = 360.0f / NUMBER_OF_POINTS;
        for (float angle = 0; angle < 360; angle += increment) {
            float degInRad = angle * DEG2RAD;
            vertices[index++] = (float) (center.x() + radius * sin(degInRad));
            vertices[index++] = center.y();
            vertices[index++] = (float) (center.z() + radius * cos(degInRad));
        }
        return new Circle(color, vertices);
    }

    static Circle createCircleInZPlane(Color color, Point3D center, float radius) {
        float[] vertices = new float[NUMBER_OF_POINTS * VERTEX_DATA_SIZE];

        int index = 0;
        float increment = 360.0f / NUMBER_OF_POINTS;
        for (float angle = 0; angle < 360; angle += increment) {
            float degInRad = angle * DEG2RAD;
            vertices[index++] = (float) (center.x() + radius * cos(degInRad));
            vertices[index++] = (float) (center.y() + radius * sin(degInRad));
            vertices[index++] = center.z();
        }
        return new Circle(color, vertices);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public float[] getColor() {
        return color.toFloatArray();
    }

    @Override
    public FloatBuffer getPositionData() {
        return vertexBuffer;
    }
}
