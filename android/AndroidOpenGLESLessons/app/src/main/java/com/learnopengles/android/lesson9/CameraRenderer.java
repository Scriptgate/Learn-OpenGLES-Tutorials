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
import com.learnopengles.android.cube.renderer.CubeRendererChain;
import com.learnopengles.android.cube.renderer.LightCubeRenderer;
import com.learnopengles.android.cube.renderer.ModelMatrixCubeRenderer;
import com.learnopengles.android.cube.renderer.data.ColorCubeRenderer;
import com.learnopengles.android.cube.renderer.data.NormalCubeRenderer;
import com.learnopengles.android.cube.renderer.data.PositionCubeRenderer;
import com.learnopengles.android.cube.renderer.data.TextureDataCubeRenderer;
import com.learnopengles.android.cube.renderer.mvp.ModelViewCubeRenderer;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.lesson9.Circle.createCircleInXPlane;
import static com.learnopengles.android.lesson9.Circle.createCircleInYPlane;
import static com.learnopengles.android.lesson9.Circle.createCircleInZPlane;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;
import static com.learnopengles.android.program.Program.createProgram;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix;

    private ModelViewProjectionMatrix mvpMatrix;

    private Program program;
    private Program pointProgram;
    private Program lineProgram;

    private CubeRendererChain cubeRendererChain;

    private static final Color BACKGROUND_COLOR = BLACK;

    private final Context activityContext;

    private int textureDataHandle;

    private List<Cube> cubes;
    private List<Line> lines = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();
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
                .positions(generatePositionData(0.1f, 0.02f, 0.1f))
                .colors(generateColorData(WHITE, WHITE, GREY, GREY, GREY, WHITE, GREY, GREY))
                .normals(generateNormalData())
                .textures(generateTextureData())
                .build();

        cubes = new ArrayList<>();

        int squareSize = 5;
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

        light = new Light();
        circles.add(createCircleInXPlane(WHITE, new Point3D(0.0f, 0.5f, 0.0f), 0.3f));
        circles.add(createCircleInYPlane(WHITE, new Point3D(0.0f, 0.5f, 0.0f), 0.3f));
        circles.add(createCircleInZPlane(WHITE, new Point3D(0.0f, 0.5f, 0.0f), 0.3f));
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

        program = createProgram("per_pixel_vertex_shader", "per_pixel_fragment_shader", asList(POSITION, COLOR, NORMAL, TEXTURE_COORDINATE));
        pointProgram = createProgram("point_vertex_shader", "point_fragment_shader", singletonList(POSITION));
        lineProgram = createProgram("color_vertex_shader", "color_fragment_shader", asList(POSITION, COLOR));


        cubeRendererChain = new CubeRendererChain(
                asList(
                        new ModelMatrixCubeRenderer(modelMatrix),

                        new PositionCubeRenderer(program),
                        new ColorCubeRenderer(program),
                        new NormalCubeRenderer(program),
                        new TextureDataCubeRenderer(program),

                        new ModelViewCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program),

                        new LightCubeRenderer(light, program)
                )
        );


        // Load the texture
        textureDataHandle = loadTexture(activityContext, R.drawable.bumpy_bricks_public_domain);
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

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(program.getHandle(TEXTURE), 0);

        light.setIdentity();
        light.translate(new Point3D(0.5f, 0.2f, 0.5f));
        light.rotate(new Point3D(0.0f, angleInDegrees, 0.0f));
        light.translate(new Point3D(0.2f, 0.0f, 0.0f));


        light.setView(viewMatrix);

        for (Cube cube : cubes) {
            cubeRendererChain.drawCube(cube);
        }
        lineProgram.useForRendering();
        for (Line line : lines) {
            line.draw(lineProgram, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix);
        }
        for (Circle circle : circles) {
            circle.draw(lineProgram, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix);
        }

        // Draw a point to indicate the light.
        pointProgram.useForRendering();
        light.drawLight(pointProgram, mvpMatrix, viewMatrix, projectionMatrix);
    }
}
