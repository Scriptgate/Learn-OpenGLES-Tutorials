package com.learnopengles.android.common;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glVertexAttrib3f;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;

public class Light {

    private ModelMatrix lightModelMatrix = new ModelMatrix();

    /**
     * Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     * we multiply this by our transformation matrices.
     */
    private final float[] lightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    /**
     * Used to hold the current position of the light in world space (after transformation via model matrix).
     */
    private final float[] lightPosInWorldSpace = new float[4];

    /**
     * Used to hold the transformed position of the light in eye space (after transformation via modelview matrix)
     */
    private final float[] lightPosInEyeSpace = new float[4];

    /**
     * Draws a point representing the position of the light.
     */
    public void drawLight(Program program, ModelViewProjectionMatrix mvpMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        final int pointMVPMatrixHandle = program.getHandle(MVP_MATRIX);
        final int pointPositionHandle = program.getHandle(POSITION);

        // Pass in the position.
        glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        mvpMatrix.multiply(lightModelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(pointMVPMatrixHandle);

        // Draw the point.
        glDrawArrays(GL_POINTS, 0, 1);
    }

    public void setIdentity() {
        lightModelMatrix.setIdentity();
    }

    public void translate(Point3D point) {
        lightModelMatrix.translate(point);
    }

    public void rotate(Point3D rotation) {
        lightModelMatrix.rotate(rotation);
    }

    public void setView(ViewMatrix viewMatrix) {
        //TODO: lightPosInWorldSpace = lightPosInModelSpace * lightModelMatrix
        lightModelMatrix.multiplyWithVectorAndStore(lightPosInModelSpace, lightPosInWorldSpace);

        //TODO: lightPosInEyeSpace = lightPosInWorldSpace * viewMatrix
        viewMatrix.multiplyWithVectorAndStore(lightPosInWorldSpace, lightPosInEyeSpace);
    }

    public void passTo(int handle) {
        glUniform3f(handle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
    }
}
