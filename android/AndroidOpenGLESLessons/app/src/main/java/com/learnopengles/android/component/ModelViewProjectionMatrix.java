package com.learnopengles.android.component;

import static android.opengl.GLES20.glUniformMatrix4fv;

public class ModelViewProjectionMatrix {

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mvpMatrix = new float[16];

    public void multiply(ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        modelMatrix.multiplyWithMatrixAndStore(viewMatrix, mvpMatrix);
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        projectionMatrix.multiplyWithMatrixAndStore(mvpMatrix);
    }

    public void passTo(int mvpMatrixHandle) {
        glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
    }
}
