package com.learnopengles.android.lesson2;

import android.os.SystemClock;

import net.scriptgate.android.opengles.light.Light;
import net.scriptgate.android.common.Point3D;
import net.scriptgate.android.opengles.matrix.ModelMatrix;
import net.scriptgate.android.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ViewMatrix;
import net.scriptgate.android.opengles.cube.Cube;
import net.scriptgate.android.opengles.cube.CubeFactory;
import net.scriptgate.android.opengles.program.Program;
import net.scriptgate.android.opengles.renderer.RendererBase;
import net.scriptgate.android.opengles.light.renderer.LightRenderer;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static net.scriptgate.android.common.Color.*;
import static net.scriptgate.android.opengles.matrix.ViewMatrix.createViewInFrontOrigin;
import static net.scriptgate.android.opengles.cube.CubeDataFactory.generateColorData;
import static net.scriptgate.android.opengles.cube.CubeDataFactory.generateNormalData;
import static net.scriptgate.android.opengles.cube.CubeDataFactory.generatePositionDataCentered;
import static net.scriptgate.android.opengles.cube.CubeFactoryBuilder.createCubeFactory;
import static net.scriptgate.android.opengles.program.AttributeVariable.*;
import static net.scriptgate.android.opengles.program.ProgramBuilder.program;

public class LightingRenderer extends RendererBase {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "LightingRenderer";

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();

    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    private List<Cube> cubes;

    private Light light;

    private CubeRenderer renderer;
    private LightRenderer lightRenderer;

    /**
     * Initialize the model data.
     */
    public LightingRenderer() {
        // Define points for a cube.

        CubeFactory cubeFactory = createCubeFactory()
                .positions(generatePositionDataCentered(1.0f, 1.0f, 1.0f))
                .colors(generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA))
                .normals(generateNormalData())
                .build();

        cubes = new ArrayList<>();
        cubes.add(cubeFactory.createAt(4.0f, 0.0f, -7.0f));
        cubes.add(cubeFactory.createAt(-4.0f, 0.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, 4.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, -4.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, 0.0f, -5.0f));

        light = new Light();
    }

    protected String getVertexShader() {
        // TODO: Explain why we normalize the vectors, explain some of the vector math behind it all. Explain what is eye space.
        return "lesson_two_vertex_shader";
    }

    protected String getFragmentShader() {
        return "lesson_two_fragment_shader";
    }

    @Override
    public void onSurfaceCreated() {
        glClearColor(BLACK.red(), BLACK.green(), BLACK.blue(), BLACK.alpha());

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        viewMatrix.onSurfaceCreated();

        Program perVertexProgram = program()
                .withVertexShader(getVertexShader())
                .withFragmentShader(getFragmentShader())
                .withAttributes(POSITION, COLOR, NORMAL)
                .build();
        renderer = new CubeRenderer(perVertexProgram, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, light);
        lightRenderer = LightRenderer.createLightRenderer(mvpMatrix, viewMatrix, projectionMatrix);
    }

    @Override
    public void onDrawFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Calculate position of the light. Rotate and then push into the distance.
        light.setIdentity();
        light.translate(0.0f, 0.0f, -5.0f);
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(0.0f, 0.0f, 2.0f);

        light.setView(viewMatrix);

        renderer.useForRendering();

        cubes.get(0).setRotationX(angleInDegrees);
        cubes.get(1).setRotationY(angleInDegrees);
        cubes.get(2).setRotationZ(angleInDegrees);
        cubes.get(4).setRotationX(angleInDegrees);
        cubes.get(4).setRotationY(angleInDegrees);

        for (Cube cube : cubes) {
            renderer.draw(cube);
        }

        lightRenderer.useForRendering();
        lightRenderer.draw(light);
    }
}
