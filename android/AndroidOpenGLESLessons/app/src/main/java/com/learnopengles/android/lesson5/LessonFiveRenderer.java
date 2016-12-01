package com.learnopengles.android.lesson5;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.CubeBuilder;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.common.RawResourceReader.readTextFileFromRawResource;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class LessonFiveRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "LessonFiveRenderer";

    private final Context mActivityContext;

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];

    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeColors;

    /**
     * This will be used to pass in the transformation matrix.
     */
    private int mMVPMatrixHandle;

    /**
     * This will be used to pass in model position information.
     */
    private int mPositionHandle;

    /**
     * This will be used to pass in model color information.
     */
    private int mColorHandle;

    /**
     * Size of the position data in elements.
     */
    private final int mPositionDataSize = 3;

    /**
     * Size of the color data in elements.
     */
    private final int mColorDataSize = 4;

    /**
     * This is a handle to our cube shading program.
     */
    private int mProgramHandle;

    /**
     * This will be used to switch between blending mode and regular mode.
     */
    private boolean mBlending = true;

    /**
     * Initialize the model data.
     */
    public LessonFiveRenderer(final Context activityContext) {
        mActivityContext = activityContext;

        // Define points for a cube.
        // X, Y, Z
        final Point p1p = new Point(-1.0f, 1.0f, 1.0f);
        final Point p2p = new Point(1.0f, 1.0f, 1.0f);
        final Point p3p = new Point(-1.0f, -1.0f, 1.0f);
        final Point p4p = new Point(1.0f, -1.0f, 1.0f);
        final Point p5p = new Point(-1.0f, 1.0f, -1.0f);
        final Point p6p = new Point(1.0f, 1.0f, -1.0f);
        final Point p7p = new Point(-1.0f, -1.0f, -1.0f);
        final Point p8p = new Point(1.0f, -1.0f, -1.0f);

        final float[] cubePositionData = CubeBuilder.generatePositionData(p1p, p2p, p3p, p4p, p5p, p6p, p7p, p8p);

        // Points of the cube: color information
        // R, G, B, A
        final Color p1c = new Color(1.0f, 0.0f, 0.0f, 1.0f);        // red
        final Color p2c = new Color(1.0f, 0.0f, 1.0f, 1.0f);        // magenta
        final Color p3c = new Color(0.0f, 0.0f, 0.0f, 1.0f);        // black
        final Color p4c = new Color(0.0f, 0.0f, 1.0f, 1.0f);        // blue
        final Color p5c = new Color(1.0f, 1.0f, 0.0f, 1.0f);        // yellow
        final Color p6c = new Color(1.0f, 1.0f, 1.0f, 1.0f);        // white
        final Color p7c = new Color(0.0f, 1.0f, 0.0f, 1.0f);        // green
        final Color p8c = new Color(0.0f, 1.0f, 1.0f, 1.0f);        // cyan

        final float[] cubeColorData = CubeBuilder.generateColorData(p1c, p2c, p3c, p4c, p5c, p6c, p7c, p8c);

        // Initialize the buffers.
        mCubePositions = allocateBuffer(cubePositionData);
        mCubeColors = allocateBuffer(cubeColorData);
    }

    protected String getVertexShader() {
        return readTextFileFromRawResource(mActivityContext, R.raw.color_vertex_shader);
    }

    protected String getFragmentShader() {
        return readTextFileFromRawResource(mActivityContext, R.raw.color_fragment_shader);
    }

    public void switchMode() {
        mBlending = !mBlending;

        if (mBlending) {
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

        // Position the eye in front of the origin.
        final Point eye = new Point(0.0f, 0.0f, -0.5f);

        // We are looking toward the distance
        final Point look = new Point(0.0f, 0.0f, -5.0f);

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final Point up = new Point(0.0f, 1.0f, 0.0f);

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);

        final String vertexShader = getVertexShader();
        final String fragmentShader = getFragmentShader();

        final int vertexShaderHandle = ShaderHelper.compileShader(GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"a_Position", "a_Color"});
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

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        if (mBlending) {
            glClear(GL_COLOR_BUFFER_BIT);
        } else {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our program
        glUseProgram(mProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mPositionHandle = glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = glGetAttribLocation(mProgramHandle, "a_Color");

        // Draw some cubes.        
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 4.0f, 0.0f, -7.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, -4.0f, 0.0f, -7.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 4.0f, -7.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -4.0f, -7.0f);
        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
        drawCube();
    }

    /**
     * Draws a cube.
     */
    private void drawCube() {
        // Pass in the position information
        mCubePositions.position(0);
        glVertexAttribPointer(mPositionHandle, mPositionDataSize, GL_FLOAT, false, 0, mCubePositions);

        glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        mCubeColors.position(0);
        glVertexAttribPointer(mColorHandle, mColorDataSize, GL_FLOAT, false, 0, mCubeColors);

        glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}
