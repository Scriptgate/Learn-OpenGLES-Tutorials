package com.learnopengles.android.component;

import android.opengl.Matrix;

import com.learnopengles.android.common.Point3D;

public class ViewMatrix {

    private final Point3D eye;
    private final Point3D look;
    private final Point3D up;

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] viewMatrix = new float[16];

    public ViewMatrix(Point3D eye, Point3D look, Point3D up) {
        this.eye = eye;
        this.look = look;
        this.up = up;
    }

    public static ViewMatrix createViewInFrontOrigin() {
        // Position the eye in front of the origin.
        Point3D eye = new Point3D(0.0f, 0.0f, -0.5f);
        return createViewMatrix(eye);
    }

    public static ViewMatrix createViewBehindOrigin() {
        // Position the eye behind the origin.
        Point3D eye = new Point3D(0.0f, 0.0f, 1.5f);
        return createViewMatrix(eye);
    }

    private static ViewMatrix createViewMatrix(Point3D eye) {
        // We are looking toward the distance
        Point3D look = new Point3D(0.0f, 0.0f, -5.0f);

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        return new ViewMatrix(eye, look, up);
    }

    public void onSurfaceCreated() {
        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);
    }

    public void multiplyWithMatrixAndStore(float[] matrix, float[] resultMatrix) {
        Matrix.multiplyMM(resultMatrix, 0, viewMatrix, 0, matrix, 0);
    }

    public void multiplyWithVectorAndStore(float[] vector, float[] resultVector) {
        Matrix.multiplyMV(resultVector, 0, viewMatrix, 0, vector, 0);
    }
}
