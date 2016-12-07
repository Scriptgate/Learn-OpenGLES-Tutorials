package com.learnopengles.android.lesson1;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.common.Point;
import com.learnopengles.android.component.ProjectionMatrix;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUseProgram;
import static com.learnopengles.android.common.Color.BLACK;
import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.CYAN;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.GREY;
import static com.learnopengles.android.common.Color.MAGENTA;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.Color.WHITE;
import static com.learnopengles.android.common.Color.YELLOW;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.lesson1.Program.createProgram;
import static com.learnopengles.android.lesson1.TriangleBuilder.triangle;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class BasicDrawingRenderer implements GLSurfaceView.Renderer {
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

    private ProjectionMatrix projectionMatrix = createProjectionMatrix();

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mvpMatrix = new float[16];

    private final List<Triangle> triangles = new ArrayList<>();

    int programHandle;

    /**
     * Initialize the model data.
     */
    public BasicDrawingRenderer() {
        // Define points for equilateral triangles.

        // Draw the triangle facing straight on.
        triangles.add(triangle()
                .equilateral(1, RED, GREEN, BLUE)
                .build());

        // Draw one translated a bit down and rotated to be flat on the ground.
        triangles.add(triangle()
                .equilateral(1, YELLOW, CYAN, MAGENTA)
                .position(new Point(0.0f, -1.0f, 0.0f))
                .rotateX(90)
                .build());

        // Draw one translated a bit to the right and rotated to be facing to the left.
        triangles.add(triangle()
                .equilateral(1, WHITE, GREY, BLACK)
                .position(new Point(1.0f, 0.0f, 0.0f))
                .rotateY(90)
                .build());
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
        projectionMatrix.onSurfaceChanged(width, height);
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
