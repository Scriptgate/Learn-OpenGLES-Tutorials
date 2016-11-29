package com.learnopengles.android.lesson6;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.RawResourceReader.readTextFileFromRawResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class LessonSixRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "LessonSixRenderer";

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
     * Store the accumulated rotation.
     */
    private final float[] mAccumulatedRotation = new float[16];

    /**
     * Store the current rotation.
     */
    private final float[] mCurrentRotation = new float[16];

    /**
     * A temporary matrix.
     */
    private float[] mTemporaryMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] mLightModelMatrix = new float[16];

    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeNormals;
    private final FloatBuffer mCubeTextureCoordinates;
    private final FloatBuffer mCubeTextureCoordinatesForPlane;

    /**
     * This will be used to pass in the transformation matrix.
     */
    private int mMVPMatrixHandle;

    /**
     * This will be used to pass in the modelview matrix.
     */
    private int mMVMatrixHandle;

    /**
     * This will be used to pass in the light position.
     */
    private int mLightPosHandle;

    /**
     * This will be used to pass in the texture.
     */
    private int mTextureUniformHandle;

    /**
     * This will be used to pass in model position information.
     */
    private int mPositionHandle;

    /**
     * This will be used to pass in model normal information.
     */
    private int mNormalHandle;

    /**
     * This will be used to pass in model texture coordinate information.
     */
    private int mTextureCoordinateHandle;

    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;

    /**
     * Size of the position data in elements.
     */
    private final int mPositionDataSize = 3;

    /**
     * Size of the normal data in elements.
     */
    private final int mNormalDataSize = 3;

    /**
     * Size of the texture coordinate data in elements.
     */
    private final int mTextureCoordinateDataSize = 2;

    /**
     * Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     * we multiply this by our transformation matrices.
     */
    private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    /**
     * Used to hold the current position of the light in world space (after transformation via model matrix).
     */
    private final float[] mLightPosInWorldSpace = new float[4];

    /**
     * Used to hold the transformed position of the light in eye space (after transformation via modelview matrix)
     */
    private final float[] mLightPosInEyeSpace = new float[4];

    /**
     * This is a handle to our cube shading program.
     */
    private int mProgramHandle;

    /**
     * This is a handle to our light point program.
     */
    private int mPointProgramHandle;

    /**
     * These are handles to our texture data.
     */
    private int mBrickDataHandle;
    private int mGrassDataHandle;

    /**
     * Temporary place to save the min and mag filter, in case the activity was restarted.
     */
    private int mQueuedMinFilter;
    private int mQueuedMagFilter;

    // These still work without volatile, but refreshes are not guaranteed to happen.
    public volatile float mDeltaX;
    public volatile float mDeltaY;

    /**
     * Initialize the model data.
     */
    public LessonSixRenderer(final Context activityContext) {
        mActivityContext = activityContext;

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

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateDataForPlane =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Right face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Left face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Top face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Bottom face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f
                };

        // Initialize the buffers.
        mCubePositions = allocateDirect(cubePositionData.length * mBytesPerFloat).order(nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeNormals = allocateDirect(cubeNormalData.length * mBytesPerFloat).order(nativeOrder()).asFloatBuffer();
        mCubeNormals.put(cubeNormalData).position(0);

        mCubeTextureCoordinates = allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat).order(nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        mCubeTextureCoordinatesForPlane = allocateDirect(cubeTextureCoordinateDataForPlane.length * mBytesPerFloat).order(nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinatesForPlane.put(cubeTextureCoordinateDataForPlane).position(0);
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
        Point eye = new Point(0.0f, 0.0f, -0.5f);

        // We are looking toward the distance
        Point look = new Point(0.0f, 0.0f, -5.0f);

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        Point up = new Point(0.0f, 1.0f, 0.0f);

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);

        final String vertexShader = readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_tex_and_light);
        final String fragmentShader = readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_tex_and_light);

        final int vertexShaderHandle = compileShader(GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});

        // Define a simple shader program for our point.
        final String pointVertexShader = readTextFileFromRawResource(mActivityContext, R.raw.point_vertex_shader);
        final String pointFragmentShader = readTextFileFromRawResource(mActivityContext, R.raw.point_fragment_shader);

        final int pointVertexShaderHandle = compileShader(GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, pointFragmentShader);
        mPointProgramHandle = createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle, new String[]{"a_Position"});

        // Load the texture
        mBrickDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.stone_wall_public_domain);
glGenerateMipmap(GL_TEXTURE_2D);

        mGrassDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.noisy_grass_public_domain);
        glGenerateMipmap(GL_TEXTURE_2D);

        if (mQueuedMinFilter != 0) {
            setMinFilter(mQueuedMinFilter);
        }

        if (mQueuedMagFilter != 0) {
            setMagFilter(mQueuedMagFilter);
        }

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);
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
        final float far = 1000.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        long slowTime = SystemClock.uptimeMillis() % 100000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        float slowAngleInDegrees = (360.0f / 100000.0f) * ((int) slowTime);

        // Set our per-vertex lighting program.
        glUseProgram(mProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mLightPosHandle = glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = glGetAttribLocation(mProgramHandle, "a_Position");
        mNormalHandle = glGetAttribLocation(mProgramHandle, "a_Normal");
        mTextureCoordinateHandle = glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -2.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 3.5f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        // Draw a cube.
        // Translate the cube into the screen.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.8f, -3.5f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.     	
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, mBrickDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(mTextureUniformHandle, 0);

        // Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GL_FLOAT, false, 0, mCubeTextureCoordinates);

        glEnableVertexAttribArray(mTextureCoordinateHandle);

        drawCube();

        // Draw a plane
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -2.0f, -5.0f);
        Matrix.scaleM(mModelMatrix, 0, 25.0f, 1.0f, 25.0f);
        Matrix.rotateM(mModelMatrix, 0, slowAngleInDegrees, 0.0f, 1.0f, 0.0f);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, mGrassDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(mTextureUniformHandle, 0);

        // Pass in the texture coordinate information
        mCubeTextureCoordinatesForPlane.position(0);
        glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GL_FLOAT, false, 0, mCubeTextureCoordinatesForPlane);

        glEnableVertexAttribArray(mTextureCoordinateHandle);

        drawCube();

        // Draw a point to indicate the light.
        glUseProgram(mPointProgramHandle);
        drawLight();
    }

    public void setMinFilter(final int filter) {
        if (mBrickDataHandle != 0 && mGrassDataHandle != 0) {
            glBindTexture(GL_TEXTURE_2D, mBrickDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
            glBindTexture(GL_TEXTURE_2D, mGrassDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        } else {
            mQueuedMinFilter = filter;
        }
    }

    public void setMagFilter(final int filter) {
        if (mBrickDataHandle != 0 && mGrassDataHandle != 0) {
            glBindTexture(GL_TEXTURE_2D, mBrickDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
            glBindTexture(GL_TEXTURE_2D, mGrassDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
        } else {
            mQueuedMagFilter = filter;
        }
    }

    /**
     * Draws a cube.
     */
    private void drawCube() {
        // Pass in the position information
        mCubePositions.position(0);
        glVertexAttribPointer(mPositionHandle, mPositionDataSize, GL_FLOAT, false, 0, mCubePositions);

        glEnableVertexAttribArray(mPositionHandle);

        // Pass in the normal information
        mCubeNormals.position(0);
        glVertexAttribPointer(mNormalHandle, mNormalDataSize, GL_FLOAT, false, 0, mCubeNormals);

        glEnableVertexAttribArray(mNormalHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).        
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        // Pass in the combined matrix.
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the light position in eye space.        
        glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    /**
     * Draws a point representing the position of the light.
     */
    private void drawLight() {
        final int pointMVPMatrixHandle = glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = glGetAttribLocation(mPointProgramHandle, "a_Position");

        // Pass in the position.
        glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);
        glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the point.
        glDrawArrays(GL_POINTS, 0, 1);
    }
}
