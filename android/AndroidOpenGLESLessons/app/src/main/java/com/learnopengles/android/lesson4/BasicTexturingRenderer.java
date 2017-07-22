package com.learnopengles.android.lesson4;

import android.content.Context;
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
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.Renderer;
import com.learnopengles.android.renderer.light.LightRenderer;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.Program.createProgram;
import static com.learnopengles.android.renderer.light.LightRendererFactory.createLightRenderer;
import static java.util.Arrays.asList;

class BasicTexturingRenderer implements Renderer {
    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "BasicTexturingRenderer";

    private final Context activityContext;

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
    BasicTexturingRenderer(final Context activityContext) {
        this.activityContext = activityContext;

        CubeDataCollection cubeData = cubeData()
                .positions(CubeDataFactory.generatePositionDataCentered(1.0f, 1.0f, 1.0f))
                .colors(generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA))
                .normals(generateNormalData())
                .textures(generateTextureData())
                .build();

        cubes = new ArrayList<>();
        cubes.add(new Cube(cubeData, new Point3D(4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(-4.0f, 0.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, 4.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, -4.0f, -7.0f)));
        cubes.add(new Cube(cubeData, new Point3D(0.0f, 0.0f, -5.0f)));

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

        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
        // Enable texture mapping
        // glEnable(GL_TEXTURE_2D);

        viewMatrix.onSurfaceCreated();

        // Load the texture
        int textureDataHandle = loadTexture(activityContext, R.drawable.bumpy_bricks_public_domain);
        for (Cube cube : cubes) {
            cube.setTexture(textureDataHandle);
        }

        Program program = createProgram("per_pixel_vertex_shader", "per_pixel_fragment_shader", asList(POSITION, COLOR, NORMAL, TEXTURE_COORDINATE));
        renderer = new CubeRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, light);
        lightRenderer = createLightRenderer(mvpMatrix, viewMatrix, projectionMatrix);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our per-vertex lighting program.
        renderer.useForRendering();

        // Calculate position of the light. Rotate and then push into the distance.
        light.setIdentity();
        light.translate(new Point3D(0.0f, 0.0f, -5.0f));
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(new Point3D(0.0f, 0.0f, 2.0f));

        light.setView(viewMatrix);

        // Draw some cubes.
        cubes.get(0).setRotationX(angleInDegrees);
        cubes.get(1).setRotationY(angleInDegrees);
        cubes.get(2).setRotationZ(angleInDegrees);
        cubes.get(4).setRotationX(angleInDegrees);
        cubes.get(4).setRotationY(angleInDegrees);

        for (Cube cube : cubes) {
            renderer.draw(cube);
        }

        // Draw a point to indicate the light.
        lightRenderer.useForRendering();
        lightRenderer.draw(light);
    }
}
