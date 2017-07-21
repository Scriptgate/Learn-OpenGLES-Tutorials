package com.learnopengles.android.lesson9;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.light.LightRenderer;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.cube.CubeDataFactory.*;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.Program.createProgram;
import static com.learnopengles.android.renderer.light.LightRendererFactory.createLightRenderer;
import static java.util.Arrays.asList;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix;

    private ModelViewProjectionMatrix mvpMatrix;

    private CubeRenderer cubeRenderer;
    private LightRenderer lightRenderer;

    private static final Color BACKGROUND_COLOR = BLACK;

    private final Context activityContext;

    private List<Cube> cubes;
    private Light light;

    public CameraRenderer(final Context activityContext) {
        this.activityContext = activityContext;
        modelMatrix = new ModelMatrix();

        float dist = 5f;

        Point3D eye = new Point3D(dist, dist, dist);
        Point3D look = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        viewMatrix = new ViewMatrix(eye, look, up);

        projectionMatrix = new IsometricProjectionMatrix(10.0f);
        mvpMatrix = new ModelViewProjectionMatrix();

        CubeDataCollection cubeData = cubeData()
                .positions(generatePositionDataCentered(0.1f, 0.02f, 0.1f))
                .colors(generateColorData(WHITE))
                .normals(generateNormalData())
                .textures(generateTextureData())
                .build();
        cubes = new ArrayList<>();

        int squareSize = 4;
        for (int j = 0; j < squareSize; j++) {
            for (int i = 0; i < squareSize; i++) {
                cubes.add(new Cube(cubeData, new Point3D(i * 0.3f, 0.0f, j * 0.3f)));
            }
        }

        light = new Light();
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
        //Instead of moving the cubes up (centering the origin), we're simply manipulating the viewMatrix
        viewMatrix.translate(new Point3D(-0.3f, 0.0f, -0.3f));

        Program program = createProgram("per_pixel_vertex_shader", "per_pixel_fragment_shader", asList(POSITION, COLOR, NORMAL, TEXTURE_COORDINATE));
        cubeRenderer = new CubeRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, light);
        lightRenderer = createLightRenderer(mvpMatrix, viewMatrix, projectionMatrix);

        // Load the texture
        int textureDataHandle = loadTexture(activityContext, R.drawable.bumpy_bricks_public_domain);
        for (Cube cube : cubes) {
            cube.setTexture(textureDataHandle);
        }
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

        light.setIdentity();
        light.translate(new Point3D(0.5f, 0.2f, 0.5f));
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(new Point3D(0.2f, 0.0f, 0.0f));
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
