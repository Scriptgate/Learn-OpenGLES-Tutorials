package com.learnopengles.android.lesson9;

import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttrib4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.FloatBufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;

public class Line {

    private FloatBuffer vertexBuffer;

    private static final int COORDS_PER_VERTEX = 3;
    private static final int VERTEX_COUNT = 2;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * BYTES_PER_FLOAT;

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

        int positionHandle = program.getHandle(POSITION);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);

        int colorHandle = program.getHandle(COLOR);
        glDisableVertexAttribArray(colorHandle);
        glVertexAttrib4fv(colorHandle, color.toArray(), 0);

        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(mvpMatrixHandle);

        glDrawArrays(GL_LINES, 0, VERTEX_COUNT);
    }
}