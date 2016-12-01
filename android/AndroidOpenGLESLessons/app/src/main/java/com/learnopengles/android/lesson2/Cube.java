package com.learnopengles.android.lesson2;

import android.opengl.Matrix;

import com.learnopengles.android.common.Point;

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
    public void drawCube(int perVertexProgramHandle, FloatBuffer cubePositions, FloatBuffer cubeNormals, FloatBuffer cubeColors, float[] mvpMatrix, float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix, float[] lightPosInEyeSpace) {
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
        Matrix.rotateM(modelMatrix, 0, rotation.x, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation.y, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation.z, 0.0f, 0.0f, 1.0f);

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

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // Pass in the modelview matrix.
        glUniformMatrix4fv(mvMatrixHandle, 1, false, mvpMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        // Pass in the combined matrix.
        glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

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
