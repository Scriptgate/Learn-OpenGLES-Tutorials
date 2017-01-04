package com.learnopengles.android.common;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.renderer.MVPWithProjectionThroughTemporaryMatrixRenderer;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.DrawArraysRenderer;
import com.learnopengles.android.renderer.MVPRenderer;
import com.learnopengles.android.renderer.VertexAttrib3fvRenderer;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glUniform3fv;
import static com.learnopengles.android.program.AttributeVariable.POSITION;

public class Light {

    private ModelMatrix modelMatrix = new ModelMatrix();

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
        new VertexAttrib3fvRenderer<>(program, POSITION, lightPosInModelSpace).apply(this);
        new MVPRenderer<>(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program).apply(this);
        new DrawArraysRenderer<>(GL_POINTS, 1).apply(this);
    }

    /**
     * Draws a point representing the position of the light.
     */
    public void drawLight(Program program, ModelViewProjectionMatrix mvpMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, float[] temporaryMatrix) {
        new VertexAttrib3fvRenderer<>(program, POSITION, lightPosInModelSpace).apply(this);
        new MVPWithProjectionThroughTemporaryMatrixRenderer<>(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program, temporaryMatrix).apply(this);
        new DrawArraysRenderer<>(GL_POINTS, 1);
    }

    public void setIdentity() {
        modelMatrix.setIdentity();
    }

    public void translate(Point3D point) {
        modelMatrix.translate(point);
    }

    public void rotate(Point3D rotation) {
        modelMatrix.rotate(rotation);
    }

    public void setView(ViewMatrix viewMatrix) {
        //TODO: lightPosInWorldSpace = lightPosInModelSpace * modelMatrix
        modelMatrix.multiplyWithVectorAndStore(lightPosInModelSpace, lightPosInWorldSpace);

        //TODO: lightPosInEyeSpace = lightPosInWorldSpace * viewMatrix
        viewMatrix.multiplyWithVectorAndStore(lightPosInWorldSpace, lightPosInEyeSpace);
    }

    public void passTo(int handle) {
        glUniform3fv(handle, 1, lightPosInEyeSpace, 0);
    }
}
