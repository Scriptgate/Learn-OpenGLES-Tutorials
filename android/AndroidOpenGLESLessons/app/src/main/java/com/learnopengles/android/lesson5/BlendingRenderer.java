package com.learnopengles.android.lesson5;

import android.os.SystemClock;

import net.scriptgate.opengles.matrix.ModelMatrix;
import net.scriptgate.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.opengles.matrix.ProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import net.scriptgate.opengles.cube.Cube;
import net.scriptgate.opengles.cube.CubeFactory;
import net.scriptgate.opengles.program.Program;
import net.scriptgate.opengles.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static net.scriptgate.common.Color.*;
import static net.scriptgate.opengles.matrix.ProjectionMatrix.createProjectionMatrix;
import static net.scriptgate.opengles.matrix.ViewMatrix.createViewInFrontOrigin;
import static net.scriptgate.opengles.cube.CubeDataFactory.generateColorData;
import static net.scriptgate.opengles.cube.CubeDataFactory.generatePositionDataCentered;
import static net.scriptgate.opengles.cube.CubeFactoryBuilder.createCubeFactory;
import static net.scriptgate.opengles.program.AttributeVariable.COLOR;
import static net.scriptgate.opengles.program.AttributeVariable.POSITION;
import static net.scriptgate.opengles.program.ProgramBuilder.program;

class BlendingRenderer implements Renderer {
    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "BlendingRenderer";

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectionMatrix();

    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    /**
     * This is a our cube shading program.
     */
    private Program program;

    /**
     * This will be used to switch between blending mode and regular mode.
     */
    private boolean blending = true;

    private List<Cube> cubes;

    private CubeRenderer renderer;

    BlendingRenderer() {

        CubeFactory cubeFactory = createCubeFactory()
                .positions(generatePositionDataCentered(1.0f, 1.0f, 1.0f))
                .colors(generateColorData(RED, MAGENTA, BLACK, BLUE, YELLOW, WHITE, GREEN, CYAN))
                .build();

        cubes = new ArrayList<>();
        cubes.add(cubeFactory.createAt(4.0f, 0.0f, -7.0f));
        cubes.add(cubeFactory.createAt(-4.0f, 0.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, 4.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, -4.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, 0.0f, -5.0f));
    }

    void switchMode() {
        blending = !blending;

        if (blending) {
            enableBlending();
        } else {
            disableBlending();
        }
    }

    private void disableBlending() {
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        glDisable(GL_BLEND);
    }

    private void enableBlending() {
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
    }

    @Override
    public void onSurfaceCreated() {
        glClearColor(BLACK.red(), BLACK.green(), BLACK.blue(), BLACK.alpha());

        enableBlending();

        viewMatrix.onSurfaceCreated();

        program = program()
                .withVertexShader("color_vertex_shader")
                .withFragmentShader("color_fragment_shader")
                .withAttributes(POSITION, COLOR)
                .build();

        renderer = new CubeRenderer(program,modelMatrix, viewMatrix, projectionMatrix, mvpMatrix);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        if (blending) {
            glClear(GL_COLOR_BUFFER_BIT);
        } else {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        program.useForRendering();

        cubes.get(0).setRotationX(angleInDegrees);
        cubes.get(1).setRotationY(angleInDegrees);
        cubes.get(2).setRotationZ(angleInDegrees);
        cubes.get(4).setRotationX(angleInDegrees);
        cubes.get(4).setRotationY(angleInDegrees);

        for (Cube cube : cubes) {
            renderer.draw(cube);
        }
    }
}
