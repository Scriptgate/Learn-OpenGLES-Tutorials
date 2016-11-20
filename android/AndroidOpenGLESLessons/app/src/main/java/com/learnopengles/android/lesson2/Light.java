package com.learnopengles.android.lesson2;

import android.opengl.Matrix;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttrib3f;

public class Light {
    /**
     * Draws a point representing the position of the light.
     *
     * @param pointProgramHandle
     * @param lightPosInModelSpace
     * @param mvpMatrix
     * @param lightModelMatrix
     * @param viewMatrix
     * @param projectionMatrix
     */
    public void drawLight(int pointProgramHandle, float[] lightPosInModelSpace, float[] mvpMatrix, float[] lightModelMatrix, float[] viewMatrix, float[] projectionMatrix) {
        final int pointMVPMatrixHandle = glGetUniformLocation(pointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = glGetAttribLocation(pointProgramHandle, "a_Position");

        // Pass in the position.
        glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, lightModelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the point.
        glDrawArrays(GL_POINTS, 0, 1);
    }
}