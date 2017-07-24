package com.learnopengles.android.lesson11;

import net.scriptgate.common.Color;
import net.scriptgate.common.Point3D;
import net.scriptgate.opengles.matrix.ModelMatrix;
import net.scriptgate.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import com.learnopengles.android.lesson9.IsometricProjectionMatrix;
import net.scriptgate.opengles.program.Program;
import net.scriptgate.opengles.renderer.RendererBase;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static net.scriptgate.common.Color.*;
import static com.learnopengles.android.lesson11.Circle.createCircleInXPlane;
import static com.learnopengles.android.lesson11.Circle.createCircleInYPlane;
import static com.learnopengles.android.lesson11.Circle.createCircleInZPlane;
import static com.learnopengles.android.lesson11.DrawableRenderer.createBasicLineRenderer;
import static com.learnopengles.android.lesson11.DrawableRenderer.createCircleRenderer;
import static net.scriptgate.opengles.program.AttributeVariable.*;
import static net.scriptgate.opengles.program.Program.createProgram;
import static java.util.Arrays.asList;

class LineRenderer extends RendererBase {

    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;

    private ModelViewProjectionMatrix mvpMatrix;

    private Program lineProgram;

    private DrawableRenderer lineRenderer;
    private DrawableRenderer circleRenderer;

    private static final Color BACKGROUND_COLOR = BLACK;

    private List<Line> lines = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();

    LineRenderer() {
        super(new IsometricProjectionMatrix(10.0f));

        modelMatrix = new ModelMatrix();

        float dist = 5f;

        Point3D eye = new Point3D(dist, dist, dist);
        Point3D look = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        viewMatrix = new ViewMatrix(eye, look, up);


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
    public void onSurfaceCreated() {
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
    public void onDrawFrame() {
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
