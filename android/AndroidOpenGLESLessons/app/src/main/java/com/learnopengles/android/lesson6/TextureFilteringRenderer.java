package com.learnopengles.android.lesson6;

import android.content.Context;
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
import com.learnopengles.android.cube.CubeDataFactory;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.Renderer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;
import static com.learnopengles.android.cube.data.CubeFactoryBuilder.createCubeFactory;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;
import static com.learnopengles.android.program.Program.createProgram;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

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

        Program program = createProgram("per_pixel_vertex_shader_tex_and_light", "per_pixel_fragment_shader_tex_and_light", asList(POSITION, NORMAL, TEXTURE_COORDINATE));
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
        light.translate(new Point3D(0.0f, 0.0f, -2.0f));
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(new Point3D(0.0f, 0.0f, 3.5f));

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
