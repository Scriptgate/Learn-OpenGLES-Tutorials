package com.learnopengles.android.lesson9;

import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.DisabledVertexAttributeArrayRenderer;
import com.learnopengles.android.renderer.EnabledVertexAttributeArrayRenderer;
import com.learnopengles.android.renderer.MVPRenderer;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glDrawArrays;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.POSITION;

public class Line {

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

        new EnabledVertexAttributeArrayRenderer<Line>(program, POSITION, vertexBuffer, VERTEX_DATA_SIZE).apply(this);
        new DisabledVertexAttributeArrayRenderer<Line>(program, COLOR, color.toArray()).apply(this);
        new MVPRenderer<Line>(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program).apply(this);

        glDrawArrays(GL_LINES, 0, 2);
    }
}