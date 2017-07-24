package com.learnopengles.android.lesson7;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.learnopengles.android.R;
import net.scriptgate.opengles.cube.CubeDataFactory;
import net.scriptgate.common.Point3D;
import net.scriptgate.opengles.matrix.ProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import net.scriptgate.opengles.program.Program;
import net.scriptgate.opengles.renderer.Renderer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.opengl.GLES20.*;
import static net.scriptgate.opengles.cube.CubeDataFactory.generateNormalData;
import static net.scriptgate.opengles.matrix.ProjectionMatrix.createProjectionMatrix;
import static net.scriptgate.opengles.texture.TextureHelper.loadTexture;
import static net.scriptgate.opengles.matrix.ViewMatrix.createViewInFrontOrigin;
import static net.scriptgate.opengles.cube.CubeDataFactory.generateTextureData;
import static net.scriptgate.opengles.program.AttributeVariable.*;
import static net.scriptgate.opengles.program.Program.createProgram;
import static net.scriptgate.opengles.program.UniformVariable.*;
import static java.util.Arrays.asList;

class VertexBufferObjectRenderer implements Renderer {
    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "VertexBufferObjectR";

    private final Activity lessonSevenActivity;
    private final GLSurfaceView glSurfaceView;

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] modelMatrix = new float[16];

    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectionMatrix(1000.0f);

    private float[] mvpMatrix = new float[16];
    private final float[] accumulatedRotation = new float[16];
    private final float[] currentRotation = new float[16];
    private float[] temporaryMatrix = new float[16];

    private float[] lightModelMatrix = new float[16];

    /**
     * Additional info for cube generation.
     */
    private int lastRequestedCubeFactor;
    private int actualCubeFactor;

    /**
     * Control whether vertex buffer objects or client-side memory will be used for rendering.
     */
    private boolean useVBOs = true;

    /**
     * Control whether strides will be used.
     */
    private boolean useStride = true;

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

    // These still work without volatile, but refreshes are not guaranteed to happen.
    volatile float deltaX;
    volatile float deltaY;

    /**
     * Thread executor for generating cube data in the background.
     */
    private final ExecutorService singleThreadedExecutor = Executors.newSingleThreadExecutor();

    /**
     * The current cubes object.
     */
    private Cubes cubes;

    /**
     * Initialize the model data.
     */
    VertexBufferObjectRenderer(final Activity lessonSevenActivity, final GLSurfaceView glSurfaceView) {
        this.lessonSevenActivity = lessonSevenActivity;
        this.glSurfaceView = glSurfaceView;
    }

    private void generateCubes(int cubeFactor, boolean toggleVbos, boolean toggleStride) {
        singleThreadedExecutor.submit(new GenDataRunnable(cubeFactor, toggleVbos, toggleStride));
    }

    private class GenDataRunnable implements Runnable {
        final int requestedCubeFactor;
        final boolean toggleVBOs;
        final boolean toggleStride;

        GenDataRunnable(int requestedCubeFactor, boolean toggleVBOs, boolean toggleStride) {
            this.requestedCubeFactor = requestedCubeFactor;
            this.toggleVBOs = toggleVBOs;
            this.toggleStride = toggleStride;
        }

        @Override
        public void run() {
            try {
                final float[] cubeNormalData = generateNormalData();

                final float[] cubeTextureCoordinateData = generateTextureData();

                final float[] cubePositionData = new float[108 * requestedCubeFactor * requestedCubeFactor * requestedCubeFactor];
                int cubePositionDataOffset = 0;

                final int segments = requestedCubeFactor + (requestedCubeFactor - 1);
                final float minPosition = -1.0f;
                final float maxPosition = 1.0f;
                final float positionRange = maxPosition - minPosition;

                for (int x = 0; x < requestedCubeFactor; x++) {
                    for (int y = 0; y < requestedCubeFactor; y++) {
                        for (int z = 0; z < requestedCubeFactor; z++) {
                            final float x1 = minPosition + ((positionRange / segments) * (x * 2));
                            final float x2 = minPosition + ((positionRange / segments) * ((x * 2) + 1));

                            final float y1 = minPosition + ((positionRange / segments) * (y * 2));
                            final float y2 = minPosition + ((positionRange / segments) * ((y * 2) + 1));

                            final float z1 = minPosition + ((positionRange / segments) * (z * 2));
                            final float z2 = minPosition + ((positionRange / segments) * ((z * 2) + 1));

                            // Define points for a cube.
                            // X, Y, Z
                            final Point3D p1p = new Point3D(x1, y2, z2);
                            final Point3D p2p = new Point3D(x2, y2, z2);
                            final Point3D p3p = new Point3D(x1, y1, z2);
                            final Point3D p4p = new Point3D(x2, y1, z2);
                            final Point3D p5p = new Point3D(x1, y2, z1);
                            final Point3D p6p = new Point3D(x2, y2, z1);
                            final Point3D p7p = new Point3D(x1, y1, z1);
                            final Point3D p8p = new Point3D(x2, y1, z1);

                            final float[] thisCubePositionData = CubeDataFactory.generatePositionData(p1p, p2p, p3p, p4p, p5p, p6p, p7p, p8p);

                            System.arraycopy(thisCubePositionData, 0, cubePositionData, cubePositionDataOffset, thisCubePositionData.length);
                            cubePositionDataOffset += thisCubePositionData.length;
                        }
                    }
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
                            boolean useVbos = useVBOs;
                            boolean useStride = VertexBufferObjectRenderer.this.useStride;

                            if (toggleVBOs) {
                                useVbos = !useVbos;
                            }

                            if (toggleStride) {
                                useStride = !useStride;
                            }

                            if (useStride) {
                                if (useVbos) {
                                    cubes = new CubesVertexBufferObjectPackedBuffers(cubePositionData, cubeNormalData, cubeTextureCoordinateData, requestedCubeFactor);
                                } else {
                                    cubes = new CubesClientSidePackedBuffer(cubePositionData, cubeNormalData, cubeTextureCoordinateData, requestedCubeFactor);
                                }
                            } else {
                                if (useVbos) {
                                    cubes = new CubesVertexBufferObjectSeparateBuffers(cubePositionData, cubeNormalData, cubeTextureCoordinateData, requestedCubeFactor);
                                } else {
                                    cubes = new CubesClientSideSeparateBuffers(cubePositionData, cubeNormalData, cubeTextureCoordinateData, requestedCubeFactor);
                                }
                            }

                            useVBOs = useVbos;
                            lessonSevenActivity.updateVboStatus(useVBOs);

                            VertexBufferObjectRenderer.this.useStride = useStride;
                            lessonSevenActivity.updateStrideStatus(VertexBufferObjectRenderer.this.useStride);

                            actualCubeFactor = requestedCubeFactor;
                        } catch (OutOfMemoryError err) {
                            if (cubes != null) {
                                cubes.release();
                                cubes = null;
                            }

                            // Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
                            System.gc();

                            lessonSevenActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//									Toast.makeText(lessonSevenActivity, "Out of memory; Dalvik takes a while to clean up the memory. Please try again.\nExternal bytes allocated=" + dalvik.system.VMRuntime.getRuntime().getExternalBytesAllocated(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            } catch (OutOfMemoryError e) {
                // Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
                System.gc();

                lessonSevenActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//						Toast.makeText(lessonSevenActivity, "Out of memory; Dalvik takes a while to clean up the memory. Please try again.\nExternal bytes allocated=" + dalvik.system.VMRuntime.getRuntime().getExternalBytesAllocated(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    void decreaseCubeCount() {
        if (lastRequestedCubeFactor > 1) {
            generateCubes(--lastRequestedCubeFactor, false, false);
        }
    }

    void increaseCubeCount() {
        if (lastRequestedCubeFactor < 16) {
            generateCubes(++lastRequestedCubeFactor, false, false);
        }
    }

    void toggleVBOs() {
        generateCubes(lastRequestedCubeFactor, true, false);
    }

    void toggleStride() {
        generateCubes(lastRequestedCubeFactor, false, true);
    }

    @Override
    public void onSurfaceCreated() {
        lastRequestedCubeFactor = actualCubeFactor = 3;
        generateCubes(actualCubeFactor, false, false);

        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        viewMatrix.onSurfaceCreated();

        program = createProgram(lessonSevenActivity, R.raw.lesson_seven_vertex_shader, R.raw.lesson_seven_fragment_shader, asList(POSITION, NORMAL, TEXTURE_COORDINATE));

        // Load the texture
        androidDataHandle = loadTexture(lessonSevenActivity, R.drawable.usb_android);
        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, androidDataHandle);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBindTexture(GL_TEXTURE_2D, androidDataHandle);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(accumulatedRotation, 0);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Set our per-vertex lighting program.
        program.useForRendering();

        // Set program handles for cube drawing.
        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        int mvMatrixHandle = program.getHandle(MV_MATRIX);
        int lightPosHandle = program.getHandle(LIGHT_POSITION);
        int textureUniformHandle = program.getHandle(TEXTURE);

        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -1.0f);

        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        viewMatrix.multiplyWithVectorAndStore(lightPosInWorldSpace, lightPosInEyeSpace);

        // Draw a cube.
        // Translate the cube into the screen.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, -3.5f);

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

        // This multiplies the view matrix by the model matrix, and stores
        // the result in the MVP matrix
        // (which currently contains model * view).
        viewMatrix.multiplyWithMatrixAndStore(modelMatrix, mvpMatrix);

        // Pass in the modelview matrix.
        glUniformMatrix4fv(mvMatrixHandle, 1, false, mvpMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix,
        // and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        projectionMatrix.multiplyWithMatrixAndStore(mvpMatrix, temporaryMatrix);
        System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

        // Pass in the combined matrix.
        glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Pass in the light position in eye space.
        glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

        // Pass in the texture information
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, androidDataHandle);

        // Tell the texture uniform sampler to use this texture in the
        // shader by binding to texture unit 0.
        glUniform1i(textureUniformHandle, 0);

        if (cubes != null) {
            cubes.render(program, actualCubeFactor);
        }
    }
}
