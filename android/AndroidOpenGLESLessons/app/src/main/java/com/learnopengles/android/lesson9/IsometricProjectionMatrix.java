package com.learnopengles.android.lesson9;


import android.opengl.Matrix;

import com.learnopengles.android.component.ProjectionMatrix;

import static android.opengl.GLES20.glViewport;

public class IsometricProjectionMatrix extends ProjectionMatrix {
    public IsometricProjectionMatrix(float far) {
        super(far);
    }

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

        final float zoom = 3.0f;

        Matrix.orthoM(projectionMatrix, 0, left*zoom, right*zoom, bottom*zoom, top*zoom, near, far);
    }



}
