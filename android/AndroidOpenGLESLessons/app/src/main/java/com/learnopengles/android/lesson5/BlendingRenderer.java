package com.learnopengles.android.lesson5;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.CubeDataFactory;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.Program.createProgram;
import static java.util.Arrays.asList;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class BlendingRenderer implements GLSurfaceView.Renderer {
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

    /**
     * Initialize the model data.
     */
    public BlendingRenderer() {

        CubeDataCollection cubeData = cubeData()
                .positions(CubeDataFactory.generatePositionDataCentered(1.0f, 1.0f, 1.0f))
                .colors(generateColorData(RED, MAGENTA, BLACK, BLUE, YELLOW, WHITE, GREEN, CYAN))
                .build();

        cubes = new ArrayList<>();
        cubes.add(new Cube(cubeData, new Point3D(4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(-4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, 4.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, -4.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, 0.0f, -5.0f)));
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
        // Cull back faces
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // Disable blending
        glDisable(GL_BLEND);
    }

    private void enableBlending() {
        // No culling of back faces
        glDisable(GL_CULL_FACE);

        // No depth testing
        glDisable(GL_DEPTH_TEST);

        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        enableBlending();

        viewMatrix.onSurfaceCreated();

        program = createProgram("color_vertex_shader", "color_fragment_shader", asList(POSITION, COLOR));

        renderer = new CubeRenderer(program,modelMatrix, viewMatrix, projectionMatrix, mvpMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        if (blending) {
            glClear(GL_COLOR_BUFFER_BIT);
        } else {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our program
        program.useForRendering();

        // Draw some cubes.
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
