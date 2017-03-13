package com.learnopengles.android.component;

import android.opengl.Matrix;

import static android.opengl.GLES20.glViewport;

public class ProjectionMatrix {

    protected final float far;

    public ProjectionMatrix(float far) {
        this.far = far;
    }

    public static ProjectionMatrix createProjectionMatrix() {
        return new ProjectionMatrix(10.0f);
    }

    public static ProjectionMatrix createProjectionMatrix(float far) {
        return new ProjectionMatrix(far);
    }

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    protected float[] projectionMatrix = new float[16];

    public void onSurfaceChanged(int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void multiplyWithMatrixAndStore(float[] matrix) {
        Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, matrix, 0);
    }

    public void multiplyWithMatrixAndStore(float[] matrix, float[] resultMatrix) {
        Matrix.multiplyMM(resultMatrix, 0, projectionMatrix, 0, matrix, 0);
    }
}
