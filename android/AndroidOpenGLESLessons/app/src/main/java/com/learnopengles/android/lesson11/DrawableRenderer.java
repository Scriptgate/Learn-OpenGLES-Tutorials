package com.learnopengles.android.lesson11;

import net.scriptgate.android.opengles.matrix.ModelMatrix;
import net.scriptgate.android.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ViewMatrix;
import net.scriptgate.android.opengles.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.lesson11.Circle.NUMBER_OF_POINTS;
import static net.scriptgate.android.opengles.program.AttributeVariable.*;
import static net.scriptgate.android.opengles.program.UniformVariable.MVP_MATRIX;

abstract class DrawableRenderer {

    private Program program;
    private final ModelMatrix modelMatrix;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final ModelViewProjectionMatrix mvpMatrix;

    private DrawableRenderer(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix) {
        this.program = program;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mvpMatrix = mvpMatrix;
    }

    void draw(Drawable drawable) {
        modelMatrix.setIdentity();

        FloatBuffer data = drawable.getPositionData();
        data.position(0);
        program.pass(data).to(POSITION);

        int colorHandle = program.getHandle(COLOR);
        glDisableVertexAttribArray(colorHandle);
        glVertexAttrib4fv(colorHandle, drawable.getColor(), 0);

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        drawArrays();
    }

    abstract void drawArrays();

    static DrawableRenderer createBasicLineRenderer(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix) {
        return new DrawableRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix) {

            @Override
            void drawArrays() {
                glDrawArrays(GL_LINES, 0, 2);
            }
        };
    }

    static DrawableRenderer createCircleRenderer(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix) {
        return new DrawableRenderer(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix) {
            @Override
            void drawArrays() {
                glDrawArrays(GL_TRIANGLE_FAN, 0, NUMBER_OF_POINTS);
            }
        };
    }

}
