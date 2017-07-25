package com.learnopengles.android.lesson7b;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.learnopengles.android.R;
import net.scriptgate.common.Point3D;
import net.scriptgate.opengles.matrix.ModelMatrix;
import net.scriptgate.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.opengles.matrix.ProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import net.scriptgate.opengles.cube.CubeDataFactory;
import com.learnopengles.android.lesson9.IsometricProjectionMatrix;

import net.scriptgate.opengles.program.Program;
import net.scriptgate.opengles.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.opengl.GLES20.*;
import static net.scriptgate.common.Color.BLACK;
import static net.scriptgate.opengles.program.ProgramBuilder.program;
import static net.scriptgate.opengles.texture.TextureHelper.loadTexture;
import static net.scriptgate.opengles.cube.CubeDataFactory.generateNormalData;
import static net.scriptgate.opengles.cube.CubeDataFactory.generateTextureData;
import static net.scriptgate.opengles.program.AttributeVariable.*;
import static net.scriptgate.opengles.program.UniformVariable.*;
import static java.util.Arrays.asList;

class VertexBufferObjectRenderer implements Renderer {
    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "VertexBufferObjectR";

    private final Context activityContext;
    private final GLSurfaceView glSurfaceView;

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private ModelMatrix modelMatrix = new ModelMatrix();

    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix = new IsometricProjectionMatrix(100.0f);

    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    private float[] lightModelMatrix = new float[16];

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

    private Program program;

    /**
     * These are handles to our texture data.
     */
    private int androidDataHandle;

    /**
     * Thread executor for generating cube data in the background.
     */
    private final ExecutorService singleThreadedExecutor = Executors.newSingleThreadExecutor();

    /**
     * The current cubes object.
     */
    private CubesVertexBufferObjectPackedBuffers cubes;

    private List<Point3D> cubePositions;

    /**
     * Initialize the model data.
     */
    VertexBufferObjectRenderer(final Context activityContext, final GLSurfaceView glSurfaceView) {
        this.activityContext = activityContext;
        this.glSurfaceView = glSurfaceView;

        cubePositions = asList(
                new Point3D(0, 0, 0),
                new Point3D(1, 0, 0),
                new Point3D(0, 0, 1),
                new Point3D(1, 0, 1)
        );

        float dist = 5;

        Point3D eye = new Point3D(dist, dist, dist);
        Point3D look = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        viewMatrix = new ViewMatrix(eye, look, up);
    }

    private void generateCubes() {
        singleThreadedExecutor.submit(new GenDataRunnable(new ArrayList<>(cubePositions)));
    }

    private class GenDataRunnable implements Runnable {

        private List<Point3D> cubePositions;

        GenDataRunnable(List<Point3D> cubePositions) {
            this.cubePositions = cubePositions;
        }

        @Override
        public void run() {
            try {
                final float[] cubeNormalData = generateNormalData();
                final float[] cubeTextureCoordinateData = generateTextureData();

                final float[] cubePositionData = new float[108 * cubePositions.size()];
                int cubePositionDataOffset = 0;

                for (Point3D cubePosition : cubePositions) {
                    float[] thisCubePositionData = CubeDataFactory.generatePositionData(cubePosition, 1, 0.2f, 1);
                    System.arraycopy(thisCubePositionData, 0, cubePositionData, cubePositionDataOffset, thisCubePositionData.length);

                    cubePositionDataOffset += thisCubePositionData.length;
                }


                // Run on the GL thread -- the same thread the other members of the renderer run in.
                glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (cubes != null) {
                            cubes.release();
                            cubes = null;
                        }

                        // Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
                        System.gc();

                        try {

                            cubes = new CubesVertexBufferObjectPackedBuffers(cubePositionData, cubeNormalData, cubeTextureCoordinateData, cubePositions.size());
                        } catch (OutOfMemoryError err) {
                            if (cubes != null) {
                                cubes.release();
                                cubes = null;
                            }

                            // Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
                            System.gc();
                        }
                    }
                });
            } catch (OutOfMemoryError e) {
                // Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
                System.gc();
            }
        }
    }

    @Override
    public void onSurfaceCreated() {
        generateCubes();

        glClearColor(BLACK.red, BLACK.green, BLACK.blue, 0.0f);

        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        viewMatrix.onSurfaceCreated();
        viewMatrix.translate(new Point3D(-1.75f, 0.0f, 1.75f));

        program = program()
                .withVertexShader(activityContext, R.raw.lesson_seven_vertex_shader)
                .withFragmentShader(activityContext, R.raw.lesson_seven_fragment_shader)
                .withAttributes(POSITION, NORMAL, TEXTURE_COORDINATE)
                .build();
        // Load the texture
        androidDataHandle = loadTexture(activityContext, R.drawable.usb_android);
        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, androidDataHandle);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBindTexture(GL_TEXTURE_2D, androidDataHandle);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -1.0f);

        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        viewMatrix.multiplyWithVectorAndStore(lightPosInWorldSpace, lightPosInEyeSpace);

        // Set our per-vertex lighting program.
        program.useForRendering();


        // Draw a cube. Translate the cube into the screen.
        modelMatrix.setIdentity();
        modelMatrix.translate(0.0f, 0.0f, -3.5f);

        mvpMatrix.multiply(modelMatrix, viewMatrix);
        mvpMatrix.passTo(program.getHandle(MV_MATRIX));

        mvpMatrix.multiply(projectionMatrix);

        // Pass in the combined matrix.
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        passLightingData();
        passTextureData();

        if (cubes != null) {
            cubes.render(program, cubePositions.size());
        }
    }

    private void passLightingData() {
        // Pass in the light position in eye space.
        glUniform3f(program.getHandle(LIGHT_POSITION), lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
    }

    private void passTextureData() {
        // Pass in the texture information
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, androidDataHandle);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(program.getHandle(TEXTURE), 0);
    }
}
