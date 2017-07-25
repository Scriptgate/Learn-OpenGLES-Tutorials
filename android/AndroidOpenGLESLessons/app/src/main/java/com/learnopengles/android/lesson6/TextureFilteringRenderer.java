package com.learnopengles.android.lesson6;

import android.content.Context;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.learnopengles.android.R;
import net.scriptgate.opengles.light.Light;
import net.scriptgate.common.Point3D;
import net.scriptgate.opengles.matrix.ModelMatrix;
import net.scriptgate.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.opengles.matrix.ProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import net.scriptgate.opengles.cube.Cube;
import net.scriptgate.opengles.cube.CubeDataFactory;
import net.scriptgate.opengles.program.Program;
import net.scriptgate.opengles.renderer.Renderer;

import static android.opengl.GLES20.*;
import static net.scriptgate.opengles.program.ProgramBuilder.program;
import static net.scriptgate.opengles.texture.TextureHelper.loadTexture;
import static net.scriptgate.opengles.matrix.ProjectionMatrix.createProjectionMatrix;
import static net.scriptgate.opengles.matrix.ViewMatrix.createViewInFrontOrigin;
import static net.scriptgate.opengles.cube.CubeDataFactory.generateNormalData;
import static net.scriptgate.opengles.cube.CubeDataFactory.generateTextureData;
import static net.scriptgate.opengles.cube.CubeFactoryBuilder.createCubeFactory;
import static net.scriptgate.opengles.program.AttributeVariable.NORMAL;
import static net.scriptgate.opengles.program.AttributeVariable.POSITION;
import static net.scriptgate.opengles.program.AttributeVariable.TEXTURE_COORDINATE;

class TextureFilteringRenderer implements Renderer {
    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "TextureFilteringR";

    private final Context activityContext;

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectionMatrix(1000.0f);

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
    volatile float deltaX;
    volatile float deltaY;

    private Cube cube;
    private Cube plane;

    private Light light;

    private CubeRenderer cubeRenderer;
    private PlaneRenderer planeRenderer;

    /**
     * Initialize the model data.
     */
    TextureFilteringRenderer(final Context activityContext) {
        this.activityContext = activityContext;

        // Initialize the buffers.
        float[] positionData = CubeDataFactory.generatePositionDataCentered(1.0f, 1.0f, 1.0f);
        float[] normalData = generateNormalData();

        cube = createCubeFactory()
                .positions(positionData)
                .normals(normalData)
                .textures(generateTextureData())
                .build()
                .createAt(0.0f, 0.8f, -3.5f);
        plane = createCubeFactory()
                .positions(positionData)
                .normals(normalData)
                .textures(generateTextureData(25.0f, 25.0f))
                .build()
                .createAt(0.0f, -2.0f, -5.0f);
        plane.setScale(new Point3D(25.0f, 1.0f, 25.0f));
        light = new Light();
    }

    @Override
    public void onSurfaceCreated() {
        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        viewMatrix.onSurfaceCreated();


        // Define a simple shader program for our point.
        pointProgram = program()
                .withVertexShader("point_vertex_shader")
                .withFragmentShader("point_fragment_shader")
                .withAttributes(POSITION)
                .build();

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

        Program program = program()
                .withVertexShader("per_pixel_vertex_shader_tex_and_light")
                .withFragmentShader("per_pixel_fragment_shader_tex_and_light")
                .withAttributes(POSITION, NORMAL, TEXTURE_COORDINATE)
                .build();
        cubeRenderer = new CubeRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, accumulatedRotation, currentRotation, temporaryMatrix, light);
        planeRenderer = new PlaneRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, temporaryMatrix, light);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10_000L;
        long slowTime = SystemClock.uptimeMillis() % 100_000L;
        float angleInDegrees = (360.0f / 10_000.0f) * ((int) time);
        float slowAngleInDegrees = (360.0f / 100_000.0f) * ((int) slowTime);


        // Calculate position of the light. Rotate and then push into the distance.
        light.setIdentity();
        light.translate(0.0f, 0.0f, -2.0f);
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(0.0f, 0.0f, 3.5f);

        light.setView(viewMatrix);

        cubeRenderer.useForRendering();
        // Set a matrix that contains the current rotation.
        currentRotation.setIdentity();
        currentRotation.rotate(new Point3D(deltaY, deltaX, 0.0f));

        deltaX = 0.0f;
        deltaY = 0.0f;

        cubeRenderer.draw(cube);

        //TODO: cubeRenderer and planeRenderer use the same program, so this call is redundant
        planeRenderer.useForRendering();
        plane.setRotationY(slowAngleInDegrees);
        planeRenderer.draw(plane);

        // Draw a point to indicate the light.
        pointProgram.useForRendering();
        light.drawLight(pointProgram, mvpMatrix, viewMatrix, projectionMatrix, temporaryMatrix);
    }

    void setMinFilter(final int filter) {
        if (brickDataHandle != 0 && grassDataHandle != 0) {
            glBindTexture(GL_TEXTURE_2D, brickDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
            glBindTexture(GL_TEXTURE_2D, grassDataHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        } else {
            queuedMinFilter = filter;
        }
    }

    void setMagFilter(final int filter) {
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
