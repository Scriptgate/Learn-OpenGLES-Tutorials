package com.learnopengles.android.lesson9;


import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.cube.renderer.CubeRendererChain;
import com.learnopengles.android.cube.renderer.ModelMatrixCubeRenderer;
import com.learnopengles.android.cube.renderer.data.ColorCubeRenderer;
import com.learnopengles.android.cube.renderer.data.PositionCubeRenderer;
import com.learnopengles.android.cube.renderer.mvp.MVPCubeRenderer;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static com.learnopengles.android.common.Color.BLACK;
import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.CYAN;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.MAGENTA;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.Color.WHITE;
import static com.learnopengles.android.common.Color.YELLOW;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.Program.createProgram;
import static java.util.Arrays.asList;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectionMatrix();

    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    private Program program;

    private List<Cube> cubes;

    private CubeRendererChain cubeRendererChain;

    public CameraRenderer() {
        CubeDataCollection cubeData = cubeData()
                .positions(generatePositionData(1.0f, 1.0f, 1.0f))
                .colors(generateColorData(RED, MAGENTA, BLACK, BLUE, YELLOW, WHITE, GREEN, CYAN))
                .build();

        cubes = new ArrayList<>();
        cubes.add(new Cube(cubeData, new Point3D(0.0f, 0.0f, -5.0f)));
    }

    protected String getVertexShader() {
        return "color_vertex_shader";
    }

    protected String getFragmentShader() {
        return "color_fragment_shader";
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // Disable blending
        glDisable(GL_BLEND);

        viewMatrix.onSurfaceCreated();

        program = createProgram(getVertexShader(), getFragmentShader(), asList(POSITION, COLOR));

        cubeRendererChain = new CubeRendererChain(
                asList(
                        new ModelMatrixCubeRenderer(modelMatrix),

                        new PositionCubeRenderer(program),
                        new ColorCubeRenderer(program),

                        new MVPCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program)

                )
        );
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our program
        program.useForRendering();

        cubes.get(0).setRotationX(angleInDegrees);
        cubes.get(0).setRotationY(angleInDegrees);

        for (Cube cube : cubes) {
            cubeRendererChain.drawCube(cube);
        }
    }
}
