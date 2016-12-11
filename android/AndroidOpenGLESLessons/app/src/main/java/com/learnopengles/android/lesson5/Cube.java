package com.learnopengles.android.lesson5;

import com.learnopengles.android.common.Point;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

public class Cube {

    private static final int POSITION_DATA_SIZE = 3;
    private static final int COLOR_DATA_SIZE = 4;

    private Point position = new Point();
    private Point rotation = new Point();

    public Cube(Point point) {
        this.position = point;
    }

    public void drawCube(int programHandle, FloatBuffer cubePositions, FloatBuffer cubeColors, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        int mvpMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        int positionHandle = glGetAttribLocation(programHandle, "a_Position");
        int colorHandle = glGetAttribLocation(programHandle, "a_Color");

        // Pass in the position information
        cubePositions.position(0);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, cubePositions);

        glEnableVertexAttribArray(positionHandle);

        // Pass in the color information
        cubeColors.position(0);
        glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GL_FLOAT, false, 0, cubeColors);

        glEnableVertexAttribArray(colorHandle);

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);

        // Pass in the combined matrix.
        mvpMatrix.passTo(mvpMatrixHandle);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void setRotationX(float rotation) {
        this.rotation.x = rotation;
    }

    public void setRotationY(float rotation) {
        this.rotation.y = rotation;
    }

    public void setRotationZ(float rotation) {
        this.rotation.z = rotation;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
