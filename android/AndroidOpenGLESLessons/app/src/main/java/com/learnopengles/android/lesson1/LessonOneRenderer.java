package com.learnopengles.android.lesson1;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.common.Point;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static com.learnopengles.android.lesson1.Color.BLACK;
import static com.learnopengles.android.lesson1.Color.BLUE;
import static com.learnopengles.android.lesson1.Color.CYAN;
import static com.learnopengles.android.lesson1.Color.GREEN;
import static com.learnopengles.android.lesson1.Color.GREY;
import static com.learnopengles.android.lesson1.Color.MAGENTA;
import static com.learnopengles.android.lesson1.Color.RED;
import static com.learnopengles.android.lesson1.Color.WHITE;
import static com.learnopengles.android.lesson1.Color.YELLOW;
import static com.learnopengles.android.lesson1.Program.createProgram;
import static com.learnopengles.android.lesson1.TriangleVerticesBuilder.vertices;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class LessonOneRenderer implements GLSurfaceView.Renderer {
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] modelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] viewMatrix = new float[16];

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] projectionMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mvpMatrix = new float[16];

    private final List<Triangle> triangles = new ArrayList<>();

    int programHandle;

    /**
     * Initialize the model data.
     */
    public LessonOneRenderer() {
        // Define points for equilateral triangles.

        // This triangle is red, green, and blue.
        // Draw the triangle facing straight on.
        Triangle triangle1 = new Triangle(vertices()
                .createEquilateralTriangle(1, RED, GREEN, BLUE)
                .build()
        );
        triangles.add(triangle1);

        // This triangle is yellow, cyan, and magenta.
        // Draw one translated a bit down and rotated to be flat on the ground.
        Triangle triangle2 = new Triangle(vertices()
                .createEquilateralTriangle(1, YELLOW, CYAN, MAGENTA)
                .build()
        );
        triangle2.setPosition(new Point(0.0f, -1.0f, 0.0f));
        triangle2.setRotationX(90);
        triangles.add(triangle2);

        // This triangle is white, gray, and black.
        // Draw one translated a bit to the right and rotated to be facing to the left.
        Triangle triangle3 = new Triangle(vertices()
                .createEquilateralTriangle(1, WHITE, GREY, BLACK)
                .build());
        triangle3.setPosition(new Point(1.0f, 0.0f, 0.0f));
        triangle3.setRotationY(90);
        triangles.add(triangle3);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to gray.
        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        initializeViewMatrix(viewMatrix);

        // Create a program object and store the handle to it.
        programHandle = createProgram("lesson_one_vertex_shader", "lesson_one_fragment_shader");

        // Tell OpenGL to use this program when rendering.
        glUseProgram(programHandle);
    }

    private static void initializeViewMatrix(float[] viewMatrix) {
        // Position the eye behind the origin.
        Point eye = new Point(0.0f, 0.0f, 1.5f);

        // We are looking toward the distance
        Point look = new Point(0.0f, 0.0f, -5.0f);

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        Point up = new Point(0.0f, 1.0f, 0.0f);

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
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
        final float far = 10.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        for (Triangle triangle : triangles) {
            triangle.setRotationZ(angleInDegrees);
            triangle.draw(programHandle, mvpMatrix, viewMatrix, modelMatrix, projectionMatrix);
        }
    }
}
