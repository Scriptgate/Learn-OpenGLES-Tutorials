package com.learnopengles.android.component;

import android.opengl.Matrix;

import com.learnopengles.android.common.Point3D;

public class ModelMatrix {

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private final float[] modelMatrix = new float[16];

    public void setIdentity() {
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(modelMatrix, 0, x, y, z);
    }

    public void translate(Point3D point) {
        translate(point.x, point.y, point.z);
    }

    public void rotate(Point3D rotation) {
        Matrix.rotateM(modelMatrix, 0, rotation.x, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation.y, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation.z, 0.0f, 0.0f, 1.0f);
    }

    public void multiplyWithMatrixAndStore(float[] matrix, float[] temporaryMatrix, float[] resultMatrix) {
        Matrix.multiplyMM(temporaryMatrix, 0, modelMatrix, 0, matrix, 0);
        System.arraycopy(temporaryMatrix, 0, resultMatrix, 0, 16);
    }

    public void multiplyWithMatrixAndStore(float[] matrix, float[] temporaryMatrix) {
        multiplyWithMatrixAndStore(matrix, temporaryMatrix, modelMatrix);
    }

    public void multiplyWithMatrixAndStore(ViewMatrix viewMatrix, float[] resultMatrix) {
        //TODO: refactor into mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        viewMatrix.multiplyWithMatrixAndStore(modelMatrix, resultMatrix);
    }

    public void multiplyWithVectorAndStore(float[] vector, float[] resultVector) {
        Matrix.multiplyMV(resultVector, 0, modelMatrix, 0, vector, 0);
    }

    public void scale(Point3D scale) {
        Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);
    }
}
