package com.learnopengles.android.lesson11;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttrib4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.lesson11.Circle.NUMBER_OF_POINTS;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;

abstract class DrawableRenderer {

    private static final int VERTEX_DATA_SIZE = 3;

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

        int positionHandle = program.getHandle(AttributeVariable.POSITION);
        FloatBuffer data = drawable.getPositionData();
        data.position(0);
        glVertexAttribPointer(positionHandle, VERTEX_DATA_SIZE, GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(positionHandle);

        int colorHandle = program.getHandle(AttributeVariable.COLOR);
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
