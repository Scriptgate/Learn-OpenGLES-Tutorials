package com.learnopengles.android.lesson4;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.TextureHelper;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.common.RawResourceReader.readTextFileFromRawResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.common.TextureHelper.loadTexture;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class BasicTexturingRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "BasicTexturingRenderer";

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

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] projectionMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mvpMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] lightModelMatrix = new float[16];

    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer cubePositions;
    private final FloatBuffer cubeColors;
    private final FloatBuffer cubeNormals;
    private final FloatBuffer cubeTextureCoordinates;

    /**
     * This will be used to pass in the transformation matrix.
     */
    private int mvpMatrixHandle;

    /**
     * This will be used to pass in the modelview matrix.
     */
    private int mvMatrixHandle;

    /**
     * This will be used to pass in the light position.
     */
    private int lightPosHandle;

    /**
     * This will be used to pass in the texture.
     */
    private int textureUniformHandle;

    /**
     * This will be used to pass in model position information.
     */
    private int positionHandle;

    /**
     * This will be used to pass in model color information.
     */
    private int colorHandle;

    /**
     * This will be used to pass in model normal information.
     */
    private int normalHandle;

    /**
     * This will be used to pass in model texture coordinate information.
     */
    private int textureCoordinateHandle;

    /**
     * Size of the position data in elements.
     */
    private final int positionDataSize = 3;

    /**
     * Size of the color data in elements.
     */
    private final int colorDataSize = 4;

    /**
     * Size of the normal data in elements.
     */
    private final int normalDataSize = 3;

    /**
     * Size of the texture coordinate data in elements.
     */
    private final int textureCoordinateDataSize = 2;

    /**
     * Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     * we multiply this by our transformation matrices.
     */
    private final float[] lightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    /**
     * Used to hold the current position of the light in world space (after transformation via model matrix).
     */
    private final float[] lightPosInWorldSpace = new float[4];

    /**
     * Used to hold the transformed position of the light in eye space (after transformation via modelview matrix)
     */
    private final float[] lightPosInEyeSpace = new float[4];

    /**
     * This is a handle to our cube shading program.
     */
    private int programHandle;

    /**
     * This is a handle to our light point program.
     */
    private int pointProgramHandle;

    /**
     * This is a handle to our texture data.
     */
    private int textureDataHandle;

    /**
     * Initialize the model data.
     */
    public BasicTexturingRenderer(final Context activityContext) {
        this.activityContext = activityContext;

        // Define points for a cube.

        // X, Y, Z
        final float[] cubePositionData =
                {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        // Front face
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,

                        // Right face
                        1.0f, 1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, -1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Back face
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,

                        // Left face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, 1.0f, 1.0f,

                        // Top face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Bottom face
                        1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                };

        // R, G, B, A
        final float[] cubeColorData =
                {
                        // Front face (red)
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,

                        // Right face (green)
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,

                        // Back face (blue)
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,

                        // Left face (yellow)
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,

                        // Top face (cyan)
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,

                        // Bottom face (magenta)
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f
                };

        // X, Y, Z
        // The normal is used in light calculations and is a vector which points
        // orthogonal to the plane of the surface. For a cube model, the normals
        // should be orthogonal to the points of each face.
        final float[] cubeNormalData =
                {
                        // Front face
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        // Right face
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        // Left face
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        // Top face
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        // Bottom face
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f
                };

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateData =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Right face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Left face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Top face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Bottom face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                };

        // Initialize the buffers.
        cubePositions = allocateBuffer(cubePositionData);
        cubeColors = allocateBuffer(cubeColorData);
        cubeNormals = allocateBuffer(cubeNormalData);
        cubeTextureCoordinates = allocateBuffer(cubeTextureCoordinateData);
    }

    protected String getVertexShader() {
        return readTextFileFromRawResource(activityContext, R.raw.per_pixel_vertex_shader);
    }

    protected String getFragmentShader() {
        return readTextFileFromRawResource(activityContext, R.raw.per_pixel_fragment_shader);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
        // Enable texture mapping
        // glEnable(GL_TEXTURE_2D);

        // Position the eye in front of the origin.
        final Point eye = new Point(0.0f, 0.0f,-0.5f);

        // We are looking toward the distance
        final Point look = new Point(0.0f, 0.0f,-5.0f);

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final Point up = new Point(0.0f, 1.0f, 0.0f);

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);

        final String vertexShader = getVertexShader();
        final String fragmentShader = getFragmentShader();

        final int vertexShaderHandle = compileShader(GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, fragmentShader);

        programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"a_Position", "a_Color", "a_Normal", "a_TexCoordinate"});

        // Define a simple shader program for our point.
        final String pointVertexShader = readTextFileFromRawResource(activityContext, R.raw.point_vertex_shader);
        final String pointFragmentShader = readTextFileFromRawResource(activityContext, R.raw.point_fragment_shader);

        final int pointVertexShaderHandle = compileShader(GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, pointFragmentShader);
        pointProgramHandle = createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle, new String[]{"a_Position"});

        // Load the texture
        textureDataHandle = loadTexture(activityContext, R.drawable.bumpy_bricks_public_domain);
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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our per-vertex lighting program.
        glUseProgram(programHandle);

        // Set program handles for cube drawing.
        mvpMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        mvMatrixHandle = glGetUniformLocation(programHandle, "u_MVMatrix");
        lightPosHandle = glGetUniformLocation(programHandle, "u_LightPos");
        textureUniformHandle = glGetUniformLocation(programHandle, "u_Texture");
        positionHandle = glGetAttribLocation(programHandle, "a_Position");
        colorHandle = glGetAttribLocation(programHandle, "a_Color");
        normalHandle = glGetAttribLocation(programHandle, "a_Normal");
        textureCoordinateHandle = glGetAttribLocation(programHandle, "a_TexCoordinate");

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(textureUniformHandle, 0);

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(lightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);

        // Draw some cubes.        
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 4.0f, 0.0f, -7.0f);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
        drawCube();

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -4.0f, 0.0f, -7.0f);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        drawCube();

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, 4.0f, -7.0f);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawCube();

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, -4.0f, -7.0f);
        drawCube();

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
        drawCube();

        // Draw a point to indicate the light.
        glUseProgram(pointProgramHandle);
        drawLight();
    }

    /**
     * Draws a cube.
     */
    private void drawCube() {
        // Pass in the position information
        cubePositions.position(0);
        glVertexAttribPointer(positionHandle, positionDataSize, GL_FLOAT, false, 0, cubePositions);

        glEnableVertexAttribArray(positionHandle);

        // Pass in the color information
        cubeColors.position(0);
        glVertexAttribPointer(colorHandle, colorDataSize, GL_FLOAT, false, 0, cubeColors);

        glEnableVertexAttribArray(colorHandle);

        // Pass in the normal information
        cubeNormals.position(0);
        glVertexAttribPointer(normalHandle, normalDataSize, GL_FLOAT, false, 0, cubeNormals);

        glEnableVertexAttribArray(normalHandle);

        // Pass in the texture coordinate information
        cubeTextureCoordinates.position(0);
        glVertexAttribPointer(textureCoordinateHandle, textureCoordinateDataSize, GL_FLOAT, false, 0, cubeTextureCoordinates);

        glEnableVertexAttribArray(textureCoordinateHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // Pass in the modelview matrix.
        glUniformMatrix4fv(mvMatrixHandle, 1, false, mvpMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        // Pass in the combined matrix.
        glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Pass in the light position in eye space.        
        glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    /**
     * Draws a point representing the position of the light.
     */
    private void drawLight() {
        final int pointMVPMatrixHandle = glGetUniformLocation(pointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = glGetAttribLocation(pointProgramHandle, "a_Position");

        // Pass in the position.
        glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, lightModelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the point.
        glDrawArrays(GL_POINTS, 0, 1);
    }
}
