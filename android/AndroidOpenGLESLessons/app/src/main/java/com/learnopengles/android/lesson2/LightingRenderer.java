package com.learnopengles.android.lesson2;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.CYAN;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.MAGENTA;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.Color.YELLOW;
import static com.learnopengles.android.common.CubeBuilder.generateColorData;
import static com.learnopengles.android.common.CubeBuilder.generateNormalData;
import static com.learnopengles.android.common.CubeBuilder.generatePositionData;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.common.RawResourceReader.readShaderFileFromResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class LightingRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "LightingRenderer";

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectionMatrix();

    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    private ModelMatrix lightModelMatrix = new ModelMatrix();

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

    private List<Cube> cubes;

    /**
     * Initialize the model data.
     */
    public LightingRenderer() {
        // Define points for a cube.

        final float[] cubePositionData = generatePositionData(1.0f, 1.0f, 1.0f);

        final float[] cubeColorData = generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA);

        float[] cubeNormalData = generateNormalData();

        // Initialize the buffers.
        cubePositions = allocateBuffer(cubePositionData);
        cubeColors = allocateBuffer(cubeColorData);
        cubeNormals = allocateBuffer(cubeNormalData);

        cubes = new ArrayList<>();
        cubes.add(new Cube(new Point(4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(new Point(-4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(new Point(0.0f, 4.0f, -7.0f)));
        cubes.add(new Cube(new Point(0.0f, -4.0f, -7.0f)));
        cubes.add(new Cube(new Point(0.0f, 0.0f, -5.0f)));
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

        viewMatrix.onSurfaceCreated();

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

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);


        // Calculate position of the light. Rotate and then push into the distance.
        lightModelMatrix.setIdentity();
        lightModelMatrix.translate(new Point(0.0f, 0.0f, -5.0f));
        lightModelMatrix.rotate(new Point(0.0f,angleInDegrees,0.0f));
        lightModelMatrix.translate(new Point(0.0f, 0.0f, 2.0f));

        lightModelMatrix.multiplyWithVectorAndStore(lightPosInModelSpace, lightPosInWorldSpace);

        viewMatrix.multiplyWithVectorAndStore(lightPosInWorldSpace, lightPosInEyeSpace);

        // Set our per-vertex lighting program.
        glUseProgram(perVertexProgramHandle);

        // Draw some cubes.
        cubes.get(0).setRotationX(angleInDegrees);
        cubes.get(1).setRotationY(angleInDegrees);
        cubes.get(2).setRotationZ(angleInDegrees);
        cubes.get(4).setRotationX(angleInDegrees);
        cubes.get(4).setRotationY(angleInDegrees);

        for (Cube cube : cubes) {
            cube.drawCube(perVertexProgramHandle, cubePositions, cubeNormals, cubeColors, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, lightPosInEyeSpace);
        }

        // Draw a point to indicate the light.
        glUseProgram(pointProgramHandle);
        new Light().drawLight(pointProgramHandle, lightPosInModelSpace, mvpMatrix, lightModelMatrix, viewMatrix, projectionMatrix);
    }
}
