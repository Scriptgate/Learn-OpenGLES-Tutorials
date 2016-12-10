package com.learnopengles.android.lesson2;

import android.opengl.Matrix;

import com.learnopengles.android.common.Point;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

public class Cube {

    private static final int POSITION_DATA_SIZE = 3;
    private static final int COLOR_DATA_SIZE = 4;
    private static final int NORMAL_DATA_SIZE = 3;

    private Point position = new Point();
    private Point rotation = new Point();

    public Cube(Point point) {
        this.position = point;
    }


    /**
     * Draws a cube.
     */
    public void drawCube(int perVertexProgramHandle, FloatBuffer cubePositions, FloatBuffer cubeNormals, FloatBuffer cubeColors, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, float[] lightPosInEyeSpace) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        // Set program handles for cube drawing.
        int mvpMatrixHandle = glGetUniformLocation(perVertexProgramHandle, "u_MVPMatrix");
        int mvMatrixHandle = glGetUniformLocation(perVertexProgramHandle, "u_MVMatrix");
        int lightPosHandle = glGetUniformLocation(perVertexProgramHandle, "u_LightPos");
        int positionHandle = glGetAttribLocation(perVertexProgramHandle, "a_Position");
        int colorHandle = glGetAttribLocation(perVertexProgramHandle, "a_Color");
        int normalHandle = glGetAttribLocation(perVertexProgramHandle, "a_Normal");

        // Pass in the position information
        cubePositions.position(0);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, cubePositions);

        glEnableVertexAttribArray(positionHandle);

        // Pass in the color information
        cubeColors.position(0);
        glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GL_FLOAT, false, 0, cubeColors);

        glEnableVertexAttribArray(colorHandle);

        // Pass in the normal information
        cubeNormals.position(0);
        glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, cubeNormals);

        glEnableVertexAttribArray(normalHandle);

        mvpMatrix.multiply(modelMatrix, viewMatrix);
        mvpMatrix.passTo(mvMatrixHandle);

        mvpMatrix.multiply(projectionMatrix);
        mvpMatrix.passTo(mvpMatrixHandle);

        // Pass in the light position in eye space.
        glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

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
