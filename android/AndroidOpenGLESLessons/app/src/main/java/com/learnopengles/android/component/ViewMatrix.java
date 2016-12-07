package com.learnopengles.android.component;

import android.opengl.Matrix;

import com.learnopengles.android.common.Point;

public class ViewMatrix {

    private final Point eye;
    private final Point look;
    private final Point up;

    private ViewMatrix(Point eye, Point look, Point up) {
        this.eye = eye;
        this.look = look;
        this.up = up;
    }

    public static ViewMatrix createViewMatrix() {
        // Position the eye behind the origin.
        Point eye = new Point(0.0f, 0.0f, 1.5f);

        // We are looking toward the distance
        Point look = new Point(0.0f, 0.0f, -5.0f);

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        Point up = new Point(0.0f, 1.0f, 0.0f);

        return new ViewMatrix(eye,look,up);
    }

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] viewMatrix = new float[16];

    public void onSurfaceCreated() {
        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);
    }

    public void multiplyWithAndStore(float[] matrix, float[] result) {
        Matrix.multiplyMM(result, 0, viewMatrix, 0, matrix, 0);
    }
}
