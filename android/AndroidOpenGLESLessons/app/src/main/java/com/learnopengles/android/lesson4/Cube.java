package com.learnopengles.android.lesson4;

import android.opengl.Matrix;

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
    private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;

    public void drawCube(int programHandle, FloatBuffer cubePositions, FloatBuffer cubeColors, FloatBuffer cubeNormals, FloatBuffer cubeTextureCoordinates, float[] mvpMatrix, float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix, float[] lightPosInEyeSpace) {
        // Set program handles for cube drawing.
        int mvpMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        int mvMatrixHandle = glGetUniformLocation(programHandle, "u_MVMatrix");
        int lightPosHandle = glGetUniformLocation(programHandle, "u_LightPos");
        int positionHandle = glGetAttribLocation(programHandle, "a_Position");
        int colorHandle = glGetAttribLocation(programHandle, "a_Color");
        int normalHandle = glGetAttribLocation(programHandle, "a_Normal");
        int textureCoordinateHandle = glGetAttribLocation(programHandle, "a_TexCoordinate");

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

        // Pass in the texture coordinate information
        cubeTextureCoordinates.position(0);
        glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, cubeTextureCoordinates);

        glEnableVertexAttribArray(textureCoordinateHandle);

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

}
