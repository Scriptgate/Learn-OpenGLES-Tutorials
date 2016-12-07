package com.learnopengles.android.component;

import android.opengl.Matrix;

import com.learnopengles.android.common.Point;

public class ModelMatrix {

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private final float[] modelMatrix = new float[16];

    public void setIdentity() {
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public void translate(Point point) {
        Matrix.translateM(modelMatrix, 0, point.x, point.y, point.z);
    }

    public void rotate(Point rotation) {
        Matrix.rotateM(modelMatrix, 0, rotation.x, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation.y, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation.z, 0.0f, 0.0f, 1.0f);
    }

    public void multiplyWithMatrixAndStore(ViewMatrix viewMatrix, float[] resultMatrix) {
        //TODO: refactor into mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        viewMatrix.multiplyWithMatrixAndStore(modelMatrix, resultMatrix);
    }
}
