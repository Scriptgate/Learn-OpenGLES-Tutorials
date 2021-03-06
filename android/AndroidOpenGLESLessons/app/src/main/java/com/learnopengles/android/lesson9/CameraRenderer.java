package com.learnopengles.android.lesson9;


import android.content.Context;
import android.os.SystemClock;

import com.learnopengles.android.R;
import net.scriptgate.android.common.Color;
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
import static net.scriptgate.android.opengles.program.ProgramBuilder.program;
import static net.scriptgate.android.opengles.texture.TextureHelper.loadTexture;
import static net.scriptgate.android.opengles.cube.CubeDataFactory.*;
import static net.scriptgate.android.opengles.cube.CubeFactoryBuilder.createCubeFactory;
import static net.scriptgate.android.opengles.program.AttributeVariable.*;

class CameraRenderer extends RendererBase {

    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;

    private ModelViewProjectionMatrix mvpMatrix;

    private CubeRenderer cubeRenderer;
    private LightRenderer lightRenderer;

    private static final Color BACKGROUND_COLOR = BLACK;

    private final Context activityContext;

    private List<Cube> cubes;
    private Light light;

    CameraRenderer(final Context activityContext) {
        super(new IsometricProjectionMatrix(10.0f));
        this.activityContext = activityContext;
        modelMatrix = new ModelMatrix();

        float dist = 5f;

        Point3D eye = new Point3D(dist, dist, dist);
        Point3D look = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        viewMatrix = new ViewMatrix(eye, look, up);

        mvpMatrix = new ModelViewProjectionMatrix();

        CubeFactory cubeFactory = createCubeFactory()
                .positions(generatePositionDataCentered(0.1f, 0.02f, 0.1f))
                .colors(generateColorData(WHITE))
                .normals(generateNormalData())
                .textures(generateTextureData())
                .build();
        cubes = new ArrayList<>();

        int squareSize = 4;
        for (int j = 0; j < squareSize; j++) {
            for (int i = 0; i < squareSize; i++) {
                cubes.add(cubeFactory.createAt(i * 0.3f, 0.0f, j * 0.3f));
            }
        }

        light = new Light();
    }

    @Override
    public void onSurfaceCreated() {
        glClearColor(BACKGROUND_COLOR.red(), BACKGROUND_COLOR.green(), BACKGROUND_COLOR.blue(), BACKGROUND_COLOR.alpha());

        // Use culling to remove back faces.
        glEnable(GL_CULL_FACE);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // Disable blending
        glDisable(GL_BLEND);

        viewMatrix.onSurfaceCreated();
        //Instead of moving the cubes up (centering the origin), we're simply manipulating the viewMatrix
        viewMatrix.translate(new Point3D(-0.3f, 0.0f, -0.3f));

        Program program = program()
                .withVertexShader("per_pixel_vertex_shader")
                .withFragmentShader("per_pixel_fragment_shader")
                .withAttributes(POSITION, COLOR, NORMAL, TEXTURE_COORDINATE)
                .build();
        cubeRenderer = new CubeRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, light);
        lightRenderer = LightRenderer.createLightRenderer(mvpMatrix, viewMatrix, projectionMatrix);

        // Load the texture
        int textureDataHandle = loadTexture(activityContext, R.drawable.bumpy_bricks_public_domain);
        for (Cube cube : cubes) {
            cube.setTexture(textureDataHandle);
        }
    }

    @Override
    public void onDrawFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        light.setIdentity();
        light.translate(0.5f, 0.2f, 0.5f);
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(0.2f, 0.0f, 0.0f);
        light.setView(viewMatrix);

        cubeRenderer.useForRendering();
        for (Cube cube : cubes) {
            cubeRenderer.draw(cube);
        }

        // Draw a point to indicate the light.
        lightRenderer.useForRendering();
        lightRenderer.draw(light);
    }
}
