package com.learnopengles.android.lesson9;

import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.drawable.BasicDrawable;
import com.learnopengles.android.renderer.drawable.BasicDrawableColorRenderer;
import com.learnopengles.android.renderer.DrawArraysRenderer;
import com.learnopengles.android.renderer.VertexAttribPointerRenderer;
import com.learnopengles.android.renderer.MVPRenderer;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_LINES;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.program.AttributeVariable.POSITION;

public class Line implements BasicDrawable {

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
        new VertexAttribPointerRenderer<>(program, POSITION, vertexBuffer, VERTEX_DATA_SIZE).apply(this);
        new BasicDrawableColorRenderer(program).apply(this);
        new MVPRenderer<>(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program).apply(this);
        new DrawArraysRenderer<>(GL_LINES, 2).apply(this);
    }

    @Override
    public float[] getColor() {
        return color.toArray();
    }
}