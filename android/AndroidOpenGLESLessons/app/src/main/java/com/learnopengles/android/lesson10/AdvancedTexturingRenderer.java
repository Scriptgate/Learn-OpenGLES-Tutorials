package com.learnopengles.android.lesson10;

import android.content.Context;
import android.os.SystemClock;

import com.learnopengles.android.R;
import net.scriptgate.opengles.light.Light;
import net.scriptgate.common.Point3D;
import net.scriptgate.opengles.matrix.ModelMatrix;
import net.scriptgate.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import net.scriptgate.opengles.cube.Cube;
import net.scriptgate.opengles.cube.CubeFactory;
import net.scriptgate.opengles.program.Program;
import net.scriptgate.opengles.renderer.RendererBase;
import net.scriptgate.opengles.light.renderer.LightRenderer;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static net.scriptgate.common.Color.*;
import static net.scriptgate.opengles.program.ProgramBuilder.program;
import static net.scriptgate.opengles.texture.TextureHelper.loadTexture;
import static net.scriptgate.opengles.matrix.ViewMatrix.createViewInFrontOrigin;
import static net.scriptgate.opengles.cube.CubeDataFactory.*;
import static net.scriptgate.opengles.cube.CubeFactoryBuilder.createCubeFactory;
import static net.scriptgate.opengles.program.AttributeVariable.*;

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

        Program program = program()
                .withVertexShader("per_pixel_vertex_shader")
                .withFragmentShader("per_pixel_fragment_shader")
                .withAttributes(POSITION, COLOR, NORMAL, TEXTURE_COORDINATE)
                .build();
        renderer = new CubeRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, light);
        lightRenderer = LightRenderer.createLightRenderer(mvpMatrix, viewMatrix, projectionMatrix);
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
