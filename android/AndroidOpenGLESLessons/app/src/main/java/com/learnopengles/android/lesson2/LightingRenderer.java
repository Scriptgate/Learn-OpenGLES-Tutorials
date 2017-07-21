package com.learnopengles.android.lesson2;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.CubeDataFactory;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.light.LightRenderer;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.Program.createProgram;
import static com.learnopengles.android.renderer.light.LightRendererFactory.createLightRenderer;
import static java.util.Arrays.asList;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class LightingRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "LightingRenderer";

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();
    private ProjectionMatrix projectionMatrix = createProjectionMatrix();

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

        CubeDataCollection cubeData = cubeData()
                .positions(CubeDataFactory.generatePositionDataCentered(1.0f, 1.0f, 1.0f))
                .colors(generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA))
                .normals(generateNormalData())
                .build();

        cubes = new ArrayList<>();
        cubes.add(new Cube(cubeData, new Point3D(4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(-4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, 4.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, -4.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, 0.0f, -5.0f)));

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
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to black.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        viewMatrix.onSurfaceCreated();

        Program perVertexProgram = createProgram(getVertexShader(), getFragmentShader(), asList(POSITION, COLOR, NORMAL));
        renderer = new CubeRenderer(perVertexProgram, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, light);
        lightRenderer = createLightRenderer(mvpMatrix, viewMatrix, projectionMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Calculate position of the light. Rotate and then push into the distance.
        light.setIdentity();
        light.translate(new Point3D(0.0f, 0.0f, -5.0f));
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(new Point3D(0.0f, 0.0f, 2.0f));

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
