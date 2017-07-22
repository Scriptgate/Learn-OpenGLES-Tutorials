package com.learnopengles.android.lesson1;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.common.BufferHelper.allocateBuffer;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;


class Triangle {

    private static final int POSITION_DATA_OFFSET = 0;
    private static final int POSITION_DATA_SIZE = 3;

    private static final int COLOR_DATA_OFFSET = 3;
    private static final int COLOR_DATA_SIZE = 4;

    /**
     * How many elements per vertex.
     */
    private static final int STRIDE_BYTES = (POSITION_DATA_SIZE + COLOR_DATA_SIZE) * BYTES_PER_FLOAT;

    private final FloatBuffer vertices;
    private Point3D rotation = new Point3D();
    private Point3D position = new Point3D();

    Triangle(float[] verticesData) {
        vertices = allocateBuffer(verticesData);
    }

    void draw(Program program, ModelViewProjectionMatrix mvpMatrix, ViewMatrix viewMatrix, ModelMatrix modelMatrix, ProjectionMatrix projectionMatrix) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        // Pass in the position information

        int positionHandle = program.getHandle(POSITION);
        vertices.position(POSITION_DATA_OFFSET);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, STRIDE_BYTES, vertices);

        // Pass in the color information
        int colorHandle = program.getHandle(COLOR);
        vertices.position(COLOR_DATA_OFFSET);
        glEnableVertexAttribArray(colorHandle);
        glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GL_FLOAT, false, STRIDE_BYTES, vertices);

        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(mvpMatrixHandle);

        glDrawArrays(GL_TRIANGLES, 0, 3);

        glDisableVertexAttribArray(positionHandle);
        glDisableVertexAttribArray(colorHandle);
    }

    void setPosition(Point3D position) {
        this.position = position;
    }

    void setRotationX(float rotation) {
        this.rotation.x = rotation;
    }

    void setRotationY(float rotation) {
        this.rotation.y = rotation;
    }

    void setRotationZ(float rotation) {
        this.rotation.z = rotation;
    }
}
