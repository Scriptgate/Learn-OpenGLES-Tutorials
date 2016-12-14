package com.learnopengles.android.lesson6;

import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.UniformVariable.LIGHT_POSITION;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;
import static com.learnopengles.android.program.UniformVariable.MV_MATRIX;

public class Cube {

    private static final int POSITION_DATA_SIZE = 3;
    private static final int NORMAL_DATA_SIZE = 3;

    public void drawCube(Program program, FloatBuffer cubePositions, FloatBuffer cubeNormals, float[] mvpMatrix, float[] modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, float[] lightPosInEyeSpace, float[] temporaryMatrix) {

        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        int mvMatrixHandle = program.getHandle(MV_MATRIX);
        int lightPosHandle = program.getHandle(LIGHT_POSITION);
        int positionHandle = program.getHandle(POSITION);
        int normalHandle = program.getHandle(NORMAL);

        // Pass in the position information
        cubePositions.position(0);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, cubePositions);


        // Pass in the normal information
        cubeNormals.position(0);
        glEnableVertexAttribArray(normalHandle);
        glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, cubeNormals);


        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        viewMatrix.multiplyWithMatrixAndStore(modelMatrix, mvpMatrix);

        // Pass in the modelview matrix.
        glUniformMatrix4fv(mvMatrixHandle, 1, false, mvpMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        projectionMatrix.multiplyWithMatrixAndStore(mvpMatrix, temporaryMatrix);
        System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

        // Pass in the combined matrix.
        glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Pass in the light position in eye space.
        glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

}
