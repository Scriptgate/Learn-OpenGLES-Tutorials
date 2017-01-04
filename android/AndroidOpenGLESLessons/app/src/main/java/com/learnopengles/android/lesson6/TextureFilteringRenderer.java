package com.learnopengles.android.lesson6;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.renderer.AccumulatedRotationCubeRenderer;
import com.learnopengles.android.cube.renderer.CubeRendererChain;
import com.learnopengles.android.cube.renderer.LightCubeRenderer;
import com.learnopengles.android.cube.renderer.ModelMatrixCubeRenderer;
import com.learnopengles.android.cube.renderer.data.NormalCubeRenderer;
import com.learnopengles.android.cube.renderer.data.PositionCubeRenderer;
import com.learnopengles.android.cube.renderer.mvp.ModelViewWithProjectionThroughTemporaryMatrixCubeRenderer;
import com.learnopengles.android.cube.renderer.TextureCubeRenderer;
import com.learnopengles.android.program.Program;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;
import static com.learnopengles.android.program.Program.createProgram;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

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

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectMatrix(1000.0f);

    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    /**
     * Store the accumulated rotation.
     */
    private final float[] accumulatedRotation = new float[16];

    /**
     * Store the current rotation.
     */
    private final ModelMatrix currentRotation = new ModelMatrix();

    /**
     * A temporary matrix.
     */
    private float[] temporaryMatrix = new float[16];

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

    private Light light;

    private CubeRendererChain cubeRendererChain;
    private CubeRendererChain planeRendererChain;

    /**
     * Initialize the model data.
     */
    public TextureFilteringRenderer(final Context activityContext) {
        this.activityContext = activityContext;

        // Initialize the buffers.
        float[] positionData = generatePositionData(1.0f, 1.0f, 1.0f);
        float[] normalData = generateNormalData();

        cube = new Cube(cubeData()
                .positions(positionData)
                .normals(normalData)
                .textures(generateTextureData())
                .build(),
                new Point3D(0.0f, 0.8f, -3.5f));
        plane = new Cube(cubeData()
                .positions(positionData)
                .normals(normalData)
                .textures(generateTextureData(25.0f, 25.0f))
                .build(),
                new Point3D(0.0f, -2.0f, -5.0f));
        plane.setScale(new Point3D(25.0f, 1.0f, 25.0f));
        light = new Light();
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

        program = createProgram("per_pixel_vertex_shader_tex_and_light", "per_pixel_fragment_shader_tex_and_light", asList(POSITION, NORMAL, TEXTURE_COORDINATE));

        // Define a simple shader program for our point.
        pointProgram = createProgram("point_vertex_shader", "point_fragment_shader", singletonList(POSITION));

        // Load the texture
        brickDataHandle = loadTexture(activityContext, R.drawable.stone_wall_public_domain);
        glGenerateMipmap(GL_TEXTURE_2D);
        cube.setTexture(brickDataHandle);

        grassDataHandle = loadTexture(activityContext, R.drawable.noisy_grass_public_domain);
        glGenerateMipmap(GL_TEXTURE_2D);
        plane.setTexture(grassDataHandle);

        if (queuedMinFilter != 0) {
            setMinFilter(queuedMinFilter);
        }

        if (queuedMagFilter != 0) {
            setMagFilter(queuedMagFilter);
        }

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(accumulatedRotation, 0);

        cubeRendererChain = new CubeRendererChain(
                asList(
                        new ModelMatrixCubeRenderer(modelMatrix),

                        new AccumulatedRotationCubeRenderer(accumulatedRotation, currentRotation, temporaryMatrix, modelMatrix),

                        new PositionCubeRenderer(program),
                        new NormalCubeRenderer(program),
                        new TextureCubeRenderer(program),

                        new ModelViewWithProjectionThroughTemporaryMatrixCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program, temporaryMatrix),

                        new LightCubeRenderer(light, program)
                )
        );

        planeRendererChain = new CubeRendererChain(
                asList(
                        new ModelMatrixCubeRenderer(modelMatrix),

                        new PositionCubeRenderer(program),
                        new NormalCubeRenderer(program),
                        new TextureCubeRenderer(program),

                        new ModelViewWithProjectionThroughTemporaryMatrixCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program, temporaryMatrix),

                        new LightCubeRenderer(light, program)
                )
        );
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10_000L;
        long slowTime = SystemClock.uptimeMillis() % 100_000L;
        float angleInDegrees = (360.0f / 10_000.0f) * ((int) time);
        float slowAngleInDegrees = (360.0f / 100_000.0f) * ((int) slowTime);

        // Set our per-vertex lighting program.
        program.useForRendering();

        // Calculate position of the light. Rotate and then push into the distance.
        light.setIdentity();
        light.translate(new Point3D(0.0f, 0.0f, -2.0f));
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(new Point3D(0.0f, 0.0f, 3.5f));

        light.setView(viewMatrix);

        drawCube();
        plane.setRotationY(slowAngleInDegrees);
        planeRendererChain.drawCube(plane);

        // Draw a point to indicate the light.
        pointProgram.useForRendering();
        light.drawLight(pointProgram, mvpMatrix, viewMatrix, projectionMatrix, temporaryMatrix);
    }

    private void drawCube() {
        // Set a matrix that contains the current rotation.
        currentRotation.setIdentity();
        currentRotation.rotate(new Point3D(deltaY, deltaX, 0.0f));

        deltaX = 0.0f;
        deltaY = 0.0f;

        cubeRendererChain.drawCube(cube);
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
}
