package com.learnopengles.android.lesson1;

import android.os.SystemClock;

import net.scriptgate.common.Point3D;
import net.scriptgate.opengles.matrix.ModelMatrix;
import net.scriptgate.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import net.scriptgate.opengles.program.Program;
import net.scriptgate.opengles.renderer.RendererBase;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static net.scriptgate.common.Color.*;
import static net.scriptgate.opengles.matrix.ViewMatrix.createViewBehindOrigin;
import static net.scriptgate.opengles.program.Program.createProgram;
import static com.learnopengles.android.lesson1.TriangleBuilder.triangle;
import static net.scriptgate.opengles.program.AttributeVariable.COLOR;
import static net.scriptgate.opengles.program.AttributeVariable.POSITION;
import static java.util.Arrays.asList;

class BasicDrawingRenderer extends RendererBase {

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix = createViewBehindOrigin();
    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    private final List<Triangle> triangles = new ArrayList<>();

    private Program program;

    BasicDrawingRenderer() {
        // Draw the triangle facing straight on.
        triangles.add(triangle()
                .equilateral(1, RED, GREEN, BLUE)
                .build());

        // Draw one translated a bit down and rotated to be flat on the ground.
        triangles.add(triangle()
                .equilateral(1, YELLOW, CYAN, MAGENTA)
                .position(new Point3D(0.0f, -1.0f, 0.0f))
                .rotateX(90)
                .build());

        // Draw one translated a bit to the right and rotated to be facing to the left.
        triangles.add(triangle()
                .equilateral(1, WHITE, GREY, BLACK)
                .position(new Point3D(1.0f, 0.0f, 0.0f))
                .rotateY(90)
                .build());
    }

    @Override
    public void onSurfaceCreated() {
        // Set the background clear color to gray.
        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        viewMatrix.onSurfaceCreated();

        program = createProgram("lesson_one_vertex_shader", "lesson_one_fragment_shader", asList(POSITION, COLOR));

        program.useForRendering();
    }

    @Override
    public void onDrawFrame() {
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        for (Triangle triangle : triangles) {
            triangle.setRotationZ(angleInDegrees);
            triangle.draw(program, mvpMatrix, viewMatrix, modelMatrix, projectionMatrix);
        }
    }
}
