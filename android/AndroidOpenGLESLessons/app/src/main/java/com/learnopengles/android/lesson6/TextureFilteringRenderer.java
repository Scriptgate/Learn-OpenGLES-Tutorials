package com.learnopengles.android.lesson6;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;
import static com.learnopengles.android.program.Program.createProgram;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;
import static java.util.Arrays.asList;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class TextureFilteringRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "TextureFilteringR";

    private final Context activityContext;

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] modelMatrix = new float[16];

    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectMatrix(1000.0f);

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mvpMatrix = new float[16];

    /**
     * Store the accumulated rotation.
     */
    private final float[] accumulatedRotation = new float[16];

    /**
     * Store the current rotation.
     */
    private final float[] currentRotation = new float[16];

    /**
     * A temporary matrix.
     */
    private float[] temporaryMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] lightModelMatrix = new float[16];

    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer cubePositions;
    private final FloatBuffer cubeNormals;
    private final FloatBuffer cubeTextureCoordinates;
    private final FloatBuffer cubeTextureCoordinatesForPlane;

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
     * This is our cube shading program.
     */
    private Program program;

    /**
     * This is our light point program.
     */
    private Program pointProgram;

    /**
     * These are handles to our texture data.
     */
    private int brickDataHandle;
    private int grassDataHandle;

    /**
     * Temporary place to save the min and mag filter, in case the activity was restarted.
     */
    private int queuedMinFilter;
    private int queuedMagFilter;

    // These still work without volatile, but refreshes are not guaranteed to happen.
    public volatile float deltaX;
    public volatile float deltaY;

    private Cube cube;
    private Cube plane;

    /**
     * Initialize the model data.
     */
    public TextureFilteringRenderer(final Context activityContext) {
        this.activityContext = activityContext;

        // Define points for a cube.

        final float[] cubePositionData = generatePositionData(1.0f, 1.0f, 1.0f);

        float[] cubeNormalData = generateNormalData();

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateData = generateTextureData();

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateDataForPlane = generateTextureData(25.0f, 25.0f);

        // Initialize the buffers.
        cubePositions = allocateBuffer(cubePositionData);
        cubeNormals = allocateBuffer(cubeNormalData);
        cubeTextureCoordinates = allocateBuffer(cubeTextureCoordinateData);
        cubeTextureCoordinatesForPlane = allocateBuffer(cubeTextureCoordinateDataForPlane);

        cube = new Cube();
        plane = new Cube();
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

        viewMatrix.onSurfaceCreated();

        program = createProgram("per_pixel_vertex_shader_tex_and_light", "per_pixel_fragment_shader_tex_and_light", asList(POSITION, NORMAL, TEXTURE_COORDINATE));

        // Define a simple shader program for our point.
        pointProgram = createProgram("point_vertex_shader", "point_fragment_shader", asList(POSITION));

        // Load the texture
        brickDataHandle = loadTexture(activityContext, R.drawable.stone_wall_public_domain);
        glGenerateMipmap(GL_TEXTURE_2D);

        grassDataHandle = loadTexture(activityContext, R.drawable.noisy_grass_public_domain);
        glGenerateMipmap(GL_TEXTURE_2D);

        if (queuedMinFilter != 0) {
            setMinFilter(queuedMinFilter);
        }

        if (queuedMagFilter != 0) {
            setMagFilter(queuedMagFilter);
        }

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(accumulatedRotation, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
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
        program.useForRendering();

        // Set program handles for cube drawing.
        int textureUniformHandle = program.getHandle(TEXTURE);
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -2.0f);
        Matrix.rotateM(lightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, 3.5f);

        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        viewMatrix.multiplyWithVectorAndStore(lightPosInWorldSpace, lightPosInEyeSpace);

        // Draw a cube.
        // Translate the cube into the screen.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.8f, -3.5f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(currentRotation, 0);
        Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(currentRotation, 0, deltaY, 1.0f, 0.0f, 0.0f);
        deltaX = 0.0f;
        deltaY = 0.0f;

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0, accumulatedRotation, 0);
        System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.     	
        Matrix.multiplyMM(temporaryMatrix, 0, modelMatrix, 0, accumulatedRotation, 0);
        System.arraycopy(temporaryMatrix, 0, modelMatrix, 0, 16);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, brickDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(textureUniformHandle, 0);

        // Pass in the texture coordinate information
        cubeTextureCoordinates.position(0);

        int textureCoordinateDataSize = 2;
        glVertexAttribPointer(textureCoordinateHandle, textureCoordinateDataSize, GL_FLOAT, false, 0, cubeTextureCoordinates);

        glEnableVertexAttribArray(textureCoordinateHandle);

        cube.drawCube(program, cubePositions, cubeNormals, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, lightPosInEyeSpace, temporaryMatrix);

        // Draw a plane
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, -2.0f, -5.0f);
        Matrix.scaleM(modelMatrix, 0, 25.0f, 1.0f, 25.0f);
        Matrix.rotateM(modelMatrix, 0, slowAngleInDegrees, 0.0f, 1.0f, 0.0f);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, grassDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(textureUniformHandle, 0);

        // Pass in the texture coordinate information
        cubeTextureCoordinatesForPlane.position(0);
        glVertexAttribPointer(textureCoordinateHandle, textureCoordinateDataSize, GL_FLOAT, false, 0, cubeTextureCoordinatesForPlane);

        glEnableVertexAttribArray(textureCoordinateHandle);

        plane.drawCube(program, cubePositions, cubeNormals, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, lightPosInEyeSpace, temporaryMatrix);

        // Draw a point to indicate the light.
        pointProgram.useForRendering();
        drawLight();
    }

    public void setMinFilter(final int filter) {
        if (brickDataHandle != 0 && grassDataHandle != 0) {
            glBindTexture(GL_TEXTURE_2D, brickDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
            glBindTexture(GL_TEXTURE_2D, grassDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        } else {
            queuedMinFilter = filter;
        }
    }

    public void setMagFilter(final int filter) {
        if (brickDataHandle != 0 && grassDataHandle != 0) {
            glBindTexture(GL_TEXTURE_2D, brickDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
            glBindTexture(GL_TEXTURE_2D, grassDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
        } else {
            queuedMagFilter = filter;
        }
    }

    /**
     * Draws a point representing the position of the light.
     */
    private void drawLight() {
        final int pointMVPMatrixHandle = pointProgram.getHandle(MVP_MATRIX);
        final int pointPositionHandle = pointProgram.getHandle(POSITION);

        // Pass in the position.
        glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        viewMatrix.multiplyWithMatrixAndStore(lightModelMatrix, mvpMatrix);
        projectionMatrix.multiplyWithMatrixAndStore(mvpMatrix, temporaryMatrix);
        System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);
        glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the point.
        glDrawArrays(GL_POINTS, 0, 1);
    }
}
