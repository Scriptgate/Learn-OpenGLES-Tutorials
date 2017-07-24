package com.learnopengles.android.lesson10;

import android.content.Context;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.CubeFactory;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererBase;
import com.learnopengles.android.renderer.light.LightRenderer;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.cube.CubeDataFactory.*;
import static com.learnopengles.android.cube.CubeFactoryBuilder.createCubeFactory;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.Program.createProgram;
import static com.learnopengles.android.renderer.light.LightRendererFactory.createLightRenderer;
import static java.util.Arrays.asList;

class AdvancedTexturingRenderer extends RendererBase {
    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "BasicTexturingRenderer";

    private final Context activityContext;

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewInFrontOrigin();

    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    private List<Cube> cubes;

    private Light light;

    private CubeRenderer renderer;
    private LightRenderer lightRenderer;

    AdvancedTexturingRenderer(final Context activityContext) {
        this.activityContext = activityContext;

        CubeFactory cubeFactory = createCubeFactory()
                .positions(generatePositionDataCentered(1.0f, 1.0f, 1.0f))
                .colors(generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA))
                .normals(generateNormalData())
                .textures(generateCubeTextureData())
                .build();

        cubes = new ArrayList<>();
        cubes.add(cubeFactory.createAt(4.0f, 0.0f, -7.0f));
        cubes.add(cubeFactory.createAt(-4.0f, 0.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, 4.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, -4.0f, -7.0f));
        cubes.add(cubeFactory.createAt(0.0f, 0.0f, -5.0f));

        light = new Light();
    }

    @Override
    public void onSurfaceCreated() {
        glClearColor(BLACK.red, BLACK.green, BLACK.blue, 0.0f);

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        viewMatrix.onSurfaceCreated();

        int textureDataHandle = loadTexture(activityContext, R.drawable.cube);
        for (Cube cube : cubes) {
            cube.setTexture(textureDataHandle);
        }

        Program program = createProgram("per_pixel_vertex_shader", "per_pixel_fragment_shader", asList(POSITION, COLOR, NORMAL, TEXTURE_COORDINATE));
        renderer = new CubeRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, light);
        lightRenderer = createLightRenderer(mvpMatrix, viewMatrix, projectionMatrix);
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
        light.translate(0.0f, 0.0f, -5.0f);
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(0.0f, 0.0f, 2.0f);

        light.setView(viewMatrix);

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
