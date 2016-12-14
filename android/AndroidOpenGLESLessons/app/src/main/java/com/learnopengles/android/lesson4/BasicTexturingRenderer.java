package com.learnopengles.android.lesson4;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.Program.createProgram;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class BasicTexturingRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "BasicTexturingRenderer";

    private final Context activityContext;

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectionMatrix();

    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer cubePositions;
    private final FloatBuffer cubeColors;
    private final FloatBuffer cubeNormals;
    private final FloatBuffer cubeTextureCoordinates;

    /**
     * This is a handle to our cube shading program.
     */
    private Program program;

    /**
     * This is a handle to our light point program.
     */
    private Program pointProgram;

    /**
     * This is a handle to our texture data.
     */
    private int textureDataHandle;

    private List<Cube> cubes;

    private Light light;

    /**
     * Initialize the model data.
     */
    public BasicTexturingRenderer(final Context activityContext) {
        this.activityContext = activityContext;

        // Define points for a cube.
        final float[] cubePositionData = generatePositionData(1.0f, 1.0f, 1.0f);
        final float[] cubeColorData = generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA);
        final float[] cubeNormalData = generateNormalData();
        final float[] cubeTextureCoordinateData = generateTextureData();

        // Initialize the buffers.
        cubePositions = allocateBuffer(cubePositionData);
        cubeColors = allocateBuffer(cubeColorData);
        cubeNormals = allocateBuffer(cubeNormalData);
        cubeTextureCoordinates = allocateBuffer(cubeTextureCoordinateData);

        cubes = new ArrayList<>();
        cubes.add(new Cube(new Point3D(4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(new Point3D(-4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(new Point3D(0.0f, 4.0f, -7.0f)));
        cubes.add(new Cube(new Point3D(0.0f, -4.0f, -7.0f)));
        cubes.add(new Cube(new Point3D(0.0f, 0.0f, -5.0f)));

        light = new Light();
    }

    protected String getVertexShader() {
        return "per_pixel_vertex_shader";
    }

    protected String getFragmentShader() {
        return "per_pixel_fragment_shader";
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

        program = createProgram(getVertexShader(), getFragmentShader(), asList(POSITION, COLOR, NORMAL, TEXTURE_COORDINATE));
        pointProgram = createProgram("point_vertex_shader", "point_fragment_shader", singletonList(POSITION));

        // Load the texture
        textureDataHandle = loadTexture(activityContext, R.drawable.bumpy_bricks_public_domain);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our per-vertex lighting program.
        program.useForRendering();

        // Set program handles for cube drawing.
        int textureUniformHandle = program.getHandle(TEXTURE);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(textureUniformHandle, 0);

        // Calculate position of the light. Rotate and then push into the distance.
        light.setIdentity();
        light.translate(new Point3D(0.0f, 0.0f, -5.0f));
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(new Point3D(0.0f, 0.0f, 2.0f));

        light.setView(viewMatrix);

        // Draw some cubes.
        cubes.get(0).setRotationX(angleInDegrees);
        cubes.get(1).setRotationY(angleInDegrees);
        cubes.get(2).setRotationZ(angleInDegrees);
        cubes.get(4).setRotationX(angleInDegrees);
        cubes.get(4).setRotationY(angleInDegrees);

        for (Cube cube : cubes) {
            cube.drawCube(program, cubePositions, cubeColors, cubeNormals, cubeTextureCoordinates, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, light);
        }

        // Draw a point to indicate the light.
        pointProgram.useForRendering();
        light.drawLight(pointProgram, mvpMatrix, viewMatrix, projectionMatrix);
    }
}
