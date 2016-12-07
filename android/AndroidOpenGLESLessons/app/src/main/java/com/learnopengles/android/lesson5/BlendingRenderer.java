package com.learnopengles.android.lesson5;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.CubeBuilder;
import com.learnopengles.android.common.ProjectionMatrix;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.common.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.common.RawResourceReader.readTextFileFromRawResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class BlendingRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "BlendingRenderer";

    private final Context activityContext;

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

    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer cubePositions;
    private final FloatBuffer cubeColors;

    /**
     * This is a handle to our cube shading program.
     */
    private int programHandle;

    /**
     * This will be used to switch between blending mode and regular mode.
     */
    private boolean blending = true;

    private List<Cube> cubes;

    /**
     * Initialize the model data.
     */
    public BlendingRenderer(final Context activityContext) {
        this.activityContext = activityContext;

        final float[] cubePositionData = CubeBuilder.generatePositionData(1.0f,1.0f,1.0f);

        final float[] cubeColorData = CubeBuilder.generateColorData(RED, MAGENTA, BLACK, BLUE, YELLOW, WHITE, GREEN, CYAN);

        // Initialize the buffers.
        cubePositions = allocateBuffer(cubePositionData);
        cubeColors = allocateBuffer(cubeColorData);

        cubes = new ArrayList<>();
        cubes.add(new Cube(new Point(4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(new Point(-4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(new Point(0.0f, 4.0f, -7.0f)));
        cubes.add(new Cube(new Point(0.0f, -4.0f, -7.0f)));
        cubes.add(new Cube(new Point(0.0f, 0.0f, -5.0f)));
    }

    protected String getVertexShader() {
        return readTextFileFromRawResource(activityContext, R.raw.color_vertex_shader);
    }

    protected String getFragmentShader() {
        return readTextFileFromRawResource(activityContext, R.raw.color_fragment_shader);
    }

    public void switchMode() {
        blending = !blending;

        if (blending) {
            // No culling of back faces
            glDisable(GL_CULL_FACE);

            // No depth testing
            glDisable(GL_DEPTH_TEST);

            // Enable blending
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE);
        } else {
            // Cull back faces
            glEnable(GL_CULL_FACE);

            // Enable depth testing
            glEnable(GL_DEPTH_TEST);

            // Disable blending
            glDisable(GL_BLEND);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // No culling of back faces
        glDisable(GL_CULL_FACE);

        // No depth testing
        glDisable(GL_DEPTH_TEST);

        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
//		glBlendEquation(GL_FUNC_ADD);

        initializeViewMatrix(viewMatrix);

        final String vertexShader = getVertexShader();
        final String fragmentShader = getFragmentShader();

        final int vertexShaderHandle = compileShader(GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, fragmentShader);

        programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"a_Position", "a_Color"});
    }

    private static void initializeViewMatrix(float[] viewMatrix) {
        // Position the eye in front of the origin.
        final Point eye = new Point(0.0f, 0.0f, -0.5f);

        // We are looking toward the distance
        final Point look = new Point(0.0f, 0.0f, -5.0f);

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final Point up = new Point(0.0f, 1.0f, 0.0f);

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
        if (blending) {
            glClear(GL_COLOR_BUFFER_BIT);
        } else {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our program
        glUseProgram(programHandle);

        // Draw some cubes.
        cubes.get(0).setRotationX(angleInDegrees);
        cubes.get(1).setRotationY(angleInDegrees);
        cubes.get(2).setRotationZ(angleInDegrees);
        cubes.get(4).setRotationX(angleInDegrees);
        cubes.get(4).setRotationY(angleInDegrees);

        for (Cube cube : cubes) {
            cube.drawCube(programHandle, cubePositions, cubeColors, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix);
        }
    }
}
