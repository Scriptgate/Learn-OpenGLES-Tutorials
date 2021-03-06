package com.learnopengles.android.lesson1;

import android.os.SystemClock;

import net.scriptgate.android.opengles.matrix.ModelMatrix;
import net.scriptgate.android.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ViewMatrix;
import net.scriptgate.android.opengles.program.Program;
import net.scriptgate.android.opengles.renderer.RendererBase;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static net.scriptgate.android.common.Color.*;
import static net.scriptgate.android.opengles.matrix.ViewMatrix.createViewBehindOrigin;
import static com.learnopengles.android.lesson1.TriangleBuilder.triangle;
import static net.scriptgate.android.opengles.program.AttributeVariable.COLOR;
import static net.scriptgate.android.opengles.program.AttributeVariable.POSITION;
import static net.scriptgate.android.opengles.program.ProgramBuilder.program;

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
                .position(0.0f, -1.0f, 0.0f)
                .rotateX(90)
                .build());

        // Draw one translated a bit to the right and rotated to be facing to the left.
        triangles.add(triangle()
                .equilateral(1, WHITE, GREY, BLACK)
                .position(1.0f, 0.0f, 0.0f)
                .rotateY(90)
                .build());
    }

    @Override
    public void onSurfaceCreated() {
        glClearColor(GREY.red(), GREY.green(), GREY.blue(), GREY.alpha());

        viewMatrix.onSurfaceCreated();

        program = program()
                .withVertexShader("lesson_one_vertex_shader")
                .withFragmentShader("lesson_one_fragment_shader")
                .withAttributes(POSITION, COLOR)
                .build();

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
