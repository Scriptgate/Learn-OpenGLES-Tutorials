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

import static android.opengl.GLES20.GL_LINES;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class Line implements Drawable {

    private FloatBuffer vertexBuffer;

    private static final int VERTEX_DATA_SIZE = 3;

    private Color color = RED;

    public Line(Color color, Point3D from, Point3D to) {
        this.color = color;
        setPoints(from, to);
    }

    private void setPoints(Point3D from, Point3D to) {
        vertexBuffer = allocateBuffer(new float[]{from.x, from.y, from.z, to.x, to.y, to.z});
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        modelMatrix.setIdentity();
        new DrawablePositionRenderer(program).apply(this);
        new DrawableColorRenderer(program).apply(this);
        new MVPRenderer<>(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program).apply(this);
        new DrawArraysRenderer<>(GL_LINES, 2).apply(this);
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