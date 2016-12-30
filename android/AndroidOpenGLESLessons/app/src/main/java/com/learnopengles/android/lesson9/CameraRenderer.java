package com.learnopengles.android.lesson9;


import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.learnopengles.android.common.Color;
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

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.Program.createProgram;
import static java.util.Arrays.asList;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix;

    private ModelViewProjectionMatrix mvpMatrix;

    private Program program;

    private List<Cube> cubes;

    private CubeRendererChain cubeRendererChain;

    private List<Line> lines = new ArrayList<>();

    private static final Color BACKGROUND_COLOR = BLACK;

    public CameraRenderer() {
        modelMatrix = new ModelMatrix();

        float dist = 5;

        Point3D eye = new Point3D(dist, dist, dist);
        Point3D look = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        viewMatrix = new ViewMatrix(eye, look, up);

        projectionMatrix = new IsometricProjectionMatrix(10.0f);
        mvpMatrix = new ModelViewProjectionMatrix();

        CubeDataCollection cubeData = cubeData()
                .positions(generatePositionData(0.1f, 0.02f, 0.1f))
                .colors(generateColorData(RED, MAGENTA, BLACK, BLUE, YELLOW, WHITE, GREEN, CYAN))
                .build();

        cubes = new ArrayList<>();

        int squareSize = 4;
        for (int j = 0; j < squareSize; j++) {
            for (int i = 0; i < squareSize; i++) {
                cubes.add(new Cube(cubeData, new Point3D(i * 0.2f, 0.0f, j * 0.2f)));
            }
        }

        float height = 0.12f;
        float boundSize = 0.8f;
        lines.add(new Line(WHITE, new Point3D(0.0f, height, 0.0f), new Point3D(boundSize, height, 0.0f)));
        lines.add(new Line(WHITE, new Point3D(boundSize, height, 0.0f), new Point3D(boundSize, height, boundSize)));
        lines.add(new Line(WHITE, new Point3D(boundSize, height, boundSize), new Point3D(0.0f, height, boundSize)));
        lines.add(new Line(WHITE, new Point3D(0.0f, height, boundSize), new Point3D(0.0f, height, 0.0f)));

        lines.add(new Line(WHITE, new Point3D(0.0f, height, 0.0f), new Point3D(0.0f, height + boundSize, 0.0f)));
        lines.add(new Line(WHITE, new Point3D(boundSize, height, 0.0f), new Point3D(boundSize, height + boundSize, 0.0f)));
        lines.add(new Line(WHITE, new Point3D(boundSize, height, boundSize), new Point3D(boundSize, height + boundSize, boundSize)));
        lines.add(new Line(WHITE, new Point3D(0.0f, height, boundSize), new Point3D(0.0f, height + boundSize, boundSize)));

        height += boundSize;
        lines.add(new Line(WHITE, new Point3D(0.0f, height, 0.0f), new Point3D(boundSize, height, 0.0f)));
        lines.add(new Line(WHITE, new Point3D(boundSize, height, 0.0f), new Point3D(boundSize, height, boundSize)));
        lines.add(new Line(WHITE, new Point3D(boundSize, height, boundSize), new Point3D(0.0f, height, boundSize)));
        lines.add(new Line(WHITE, new Point3D(0.0f, height, boundSize), new Point3D(0.0f, height, 0.0f)));
    }

    protected String getVertexShader() {
        return "color_vertex_shader";
    }

    protected String getFragmentShader() {
        return "color_fragment_shader";
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(BACKGROUND_COLOR.red, BACKGROUND_COLOR.green, BACKGROUND_COLOR.blue, BACKGROUND_COLOR.alpha);

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

        for (Cube cube : cubes) {
            cubeRendererChain.drawCube(cube);
        }
        for (Line line : lines) {
            line.draw(program, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix);
        }
    }
}
