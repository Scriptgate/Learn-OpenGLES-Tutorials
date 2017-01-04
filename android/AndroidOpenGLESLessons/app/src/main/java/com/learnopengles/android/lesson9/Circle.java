package com.learnopengles.android.lesson9;


import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.drawable.Drawable;
import com.learnopengles.android.renderer.drawable.DrawableColorRenderer;
import com.learnopengles.android.renderer.DrawArraysRenderer;
import com.learnopengles.android.renderer.MVPRenderer;
import com.learnopengles.android.renderer.drawable.DrawablePositionRenderer;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Circle implements Drawable {


    private static final int NUMBER_OF_POINTS = 360;

    private FloatBuffer vertexBuffer;

    private static final int VERTEX_DATA_SIZE = 3;

    private static final float DEG2RAD = 3.14159f / 180;

    private Color color;

    private Circle(Color color, float[] vertices) {
        this.color = color;
        this.vertexBuffer = allocateBuffer(vertices);
    }

    public static Circle createCircleInXPlane(Color color, Point3D center, float radius) {
        float[] vertices = new float[NUMBER_OF_POINTS * VERTEX_DATA_SIZE];

        int index = 0;
        float increment = 360.0f / NUMBER_OF_POINTS;
        for (float angle = 0; angle < 360; angle += increment) {
            float degInRad = angle * DEG2RAD;
            vertices[index++] = center.x;
            vertices[index++] = (float) (center.y + radius * cos(degInRad));
            vertices[index++] = (float) (center.z + radius * sin(degInRad));
        }
        return new Circle(color, vertices);
    }

    public static Circle createCircleInYPlane(Color color, Point3D center, float radius) {
        float[] vertices = new float[NUMBER_OF_POINTS * VERTEX_DATA_SIZE];

        int index = 0;
        float increment = 360.0f / NUMBER_OF_POINTS;
        for (float angle = 0; angle < 360; angle += increment) {
            float degInRad = angle * DEG2RAD;
            vertices[index++] = (float) (center.x + radius * sin(degInRad));
            vertices[index++] = center.y;
            vertices[index++] = (float) (center.z + radius * cos(degInRad));
        }
        return new Circle(color, vertices);
    }

    public static Circle createCircleInZPlane(Color color, Point3D center, float radius) {
        float[] vertices = new float[NUMBER_OF_POINTS * VERTEX_DATA_SIZE];

        int index = 0;
        float increment = 360.0f / NUMBER_OF_POINTS;
        for (float angle = 0; angle < 360; angle += increment) {
            float degInRad = angle * DEG2RAD;
            vertices[index++] = (float) (center.x + radius * cos(degInRad));
            vertices[index++] = (float) (center.y + radius * sin(degInRad));
            vertices[index++] = center.z;
        }
        return new Circle(color, vertices);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        modelMatrix.setIdentity();
        new DrawablePositionRenderer(program).apply(this);
        new DrawableColorRenderer(program).apply(this);
        new MVPRenderer<>(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program).apply(this);
        new DrawArraysRenderer<>(GL_LINE_LOOP, NUMBER_OF_POINTS).apply(this);
    }

    public void fill(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        modelMatrix.setIdentity();
        new DrawablePositionRenderer(program).apply(this);
        new DrawableColorRenderer(program).apply(this);
        new MVPRenderer<>(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program).apply(this);
        new DrawArraysRenderer<>(GL_TRIANGLE_FAN, NUMBER_OF_POINTS).apply(this);
    }

    @Override
    public float[] getColor() {
        return color.toArray();
    }

    @Override
    public FloatBuffer getPositionData() {
        return vertexBuffer;
    }
}
