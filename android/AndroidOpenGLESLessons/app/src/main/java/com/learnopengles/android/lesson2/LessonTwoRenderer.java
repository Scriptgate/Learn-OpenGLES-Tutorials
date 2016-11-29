package com.learnopengles.android.lesson2;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.ShapeBuilder;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.FloatBufferConstants.BYTES_PER_FLOAT;
import static com.learnopengles.android.common.RawResourceReader.readShaderFileFromResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.lesson2.CubeVerticesBuilder.vertices;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class LessonTwoRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "LessonTwoRenderer";

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
     * This is a handle to our per-vertex cube shading program.
     */
    private int perVertexProgramHandle;

    /**
     * This is a handle to our light point program.
     */
    private int pointProgramHandle;

    private Cube cube1;
    private Cube cube2;
    private Cube cube3;
    private Cube cube4;
    private Cube cube5;

    /**
     * Initialize the model data.
     */
    public LessonTwoRenderer() {
        // Define points for a cube.

        // X, Y, Z
        final float[] cubePositionData = ShapeBuilder.generateCubeData(1.0f, 1.0f, 1.0f);

        // R, G, B, A
        final float[] cubeColorData = vertices().color();

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

        // Initialize the buffers.
        cubePositions = allocateDirect(cubePositionData.length * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
        cubePositions.put(cubePositionData).position(0);

        cubeColors = allocateDirect(cubeColorData.length * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
        cubeColors.put(cubeColorData).position(0);

        cubeNormals = allocateDirect(cubeNormalData.length * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
        cubeNormals.put(cubeNormalData).position(0);

        cube1 = new Cube();
        cube2 = new Cube();
        cube3 = new Cube();
        cube4 = new Cube();
        cube5 = new Cube();

        cube1.setPosition(new Point(4.0f, 0.0f, -7.0f));
        cube2.setPosition(new Point(-4.0f, 0.0f, -7.0f));
        cube3.setPosition(new Point(0.0f, 4.0f, -7.0f));
        cube4.setPosition(new Point(0.0f, -4.0f, -7.0f));
        cube5.setPosition(new Point(0.0f, 0.0f, -5.0f));
    }

    protected String getVertexShader() {
        // TODO: Explain why we normalize the vectors, explain some of the vector math behind it all. Explain what is eye space.
        return readShaderFileFromResource("lesson_two_vertex_shader");
    }

    protected String getFragmentShader() {
        return readShaderFileFromResource("lesson_two_fragment_shader");
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        initializeViewMatrix(viewMatrix);

        final String vertexShader = getVertexShader();
        final String fragmentShader = getFragmentShader();

        final int vertexShaderHandle = compileShader(GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, fragmentShader);

        perVertexProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"a_Position", "a_Color", "a_Normal"});

        // Define a simple shader program for our point.
        final String pointVertexShader = readShaderFileFromResource("lesson_two_point_vertex_shader");
        final String pointFragmentShader = readShaderFileFromResource("lesson_two_point_fragment_shader");

        final int pointVertexShaderHandle = compileShader(GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, pointFragmentShader);
        pointProgramHandle = createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle, new String[]{"a_Position"});
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



        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(lightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);

        // Set our per-vertex lighting program.
        glUseProgram(perVertexProgramHandle);

        // Draw some cubes.
        cube1.setRotationX(angleInDegrees);
        cube2.setRotationY(angleInDegrees);
        cube3.setRotationZ(angleInDegrees);
        cube5.setRotationX(angleInDegrees);
        cube5.setRotationY(angleInDegrees);

        cube1.drawCube(perVertexProgramHandle, cubePositions, cubeNormals, cubeColors, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, lightPosInEyeSpace);
        cube2.drawCube(perVertexProgramHandle, cubePositions, cubeNormals, cubeColors, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, lightPosInEyeSpace);
        cube3.drawCube(perVertexProgramHandle, cubePositions, cubeNormals, cubeColors, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, lightPosInEyeSpace);
        cube4.drawCube(perVertexProgramHandle, cubePositions, cubeNormals, cubeColors, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, lightPosInEyeSpace);
        cube5.drawCube(perVertexProgramHandle, cubePositions, cubeNormals, cubeColors, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, lightPosInEyeSpace);

        // Draw a point to indicate the light.
        glUseProgram(pointProgramHandle);
        new Light().drawLight(pointProgramHandle, lightPosInModelSpace, mvpMatrix, lightModelMatrix, viewMatrix, projectionMatrix);
    }
}
