package com.learnopengles.android.lesson11;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.lesson9.IsometricProjectionMatrix;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.lesson11.Circle.createCircleInXPlane;
import static com.learnopengles.android.lesson11.Circle.createCircleInYPlane;
import static com.learnopengles.android.lesson11.Circle.createCircleInZPlane;
import static com.learnopengles.android.lesson11.DrawableRenderer.createBasicLineRenderer;
import static com.learnopengles.android.lesson11.DrawableRenderer.createCircleRenderer;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.Program.createProgram;
import static java.util.Arrays.asList;

class LineRenderer implements GLSurfaceView.Renderer {

    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix;

    private ModelViewProjectionMatrix mvpMatrix;

    private Program lineProgram;

    private DrawableRenderer lineRenderer;
    private DrawableRenderer circleRenderer;

    private static final Color BACKGROUND_COLOR = BLACK;

    private List<Line> lines = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();

    LineRenderer() {

        modelMatrix = new ModelMatrix();

        float dist = 5f;

        Point3D eye = new Point3D(dist, dist, dist);
        Point3D look = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        viewMatrix = new ViewMatrix(eye, look, up);

        projectionMatrix = new IsometricProjectionMatrix(10.0f);

        mvpMatrix = new ModelViewProjectionMatrix();

        float boundSize = 0.8f;
        lines.add(new Line(WHITE, new Point3D(0.0f, 0.0f, 0.0f), new Point3D(boundSize, 0.0f, 0.0f)));
        lines.add(new Line(WHITE, new Point3D(boundSize, 0.0f, 0.0f), new Point3D(boundSize, 0.0f, boundSize)));
        lines.add(new Line(WHITE, new Point3D(boundSize, 0.0f, boundSize), new Point3D(0.0f, 0.0f, boundSize)));
        lines.add(new Line(WHITE, new Point3D(0.0f, 0.0f, boundSize), new Point3D(0.0f, 0.0f, 0.0f)));

        lines.add(new Line(WHITE, new Point3D(0.0f, 0.0f, 0.0f), new Point3D(0.0f, 0.0f + boundSize, 0.0f)));
        lines.add(new Line(WHITE, new Point3D(boundSize, 0.0f, 0.0f), new Point3D(boundSize, 0.0f + boundSize, 0.0f)));
        lines.add(new Line(WHITE, new Point3D(boundSize, 0.0f, boundSize), new Point3D(boundSize, 0.0f + boundSize, boundSize)));
        lines.add(new Line(WHITE, new Point3D(0.0f, 0.0f, boundSize), new Point3D(0.0f, 0.0f + boundSize, boundSize)));

        lines.add(new Line(WHITE, new Point3D(0.0f, boundSize, 0.0f), new Point3D(boundSize, boundSize, 0.0f)));
        lines.add(new Line(WHITE, new Point3D(boundSize, boundSize, 0.0f), new Point3D(boundSize, boundSize, boundSize)));
        lines.add(new Line(WHITE, new Point3D(boundSize, boundSize, boundSize), new Point3D(0.0f, boundSize, boundSize)));
        lines.add(new Line(WHITE, new Point3D(0.0f, boundSize, boundSize), new Point3D(0.0f, boundSize, 0.0f)));

        circles.add(createCircleInXPlane(RED, new Point3D(0.4f, 0.4f, 0.4f), 0.4f));
        circles.add(createCircleInYPlane(GREEN, new Point3D(0.4f, 0.4f, 0.4f), 0.4f));
        circles.add(createCircleInZPlane(BLUE, new Point3D(0.4f, 0.4f, 0.4f), 0.4f));
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

        lineProgram = createProgram("color_vertex_shader", "color_fragment_shader", asList(POSITION, COLOR));

        lineRenderer = createBasicLineRenderer(lineProgram, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix);

        circleRenderer = createCircleRenderer(lineProgram, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //TODO: same program in lineRenderer as circleRenderer
        lineProgram.useForRendering();
        for (Line line : lines) {
            lineRenderer.draw(line);
        }
        for (Circle circle : circles) {
            circleRenderer.draw(circle);
        }
    }
}
