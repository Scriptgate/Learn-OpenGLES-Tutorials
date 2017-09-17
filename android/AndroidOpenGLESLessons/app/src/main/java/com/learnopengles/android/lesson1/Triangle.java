package com.learnopengles.android.lesson1;

import net.scriptgate.android.common.Point3D;
import net.scriptgate.android.opengles.matrix.ModelMatrix;
import net.scriptgate.android.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ViewMatrix;
import net.scriptgate.android.opengles.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static net.scriptgate.android.nio.BufferHelper.allocateBuffer;
import static net.scriptgate.android.opengles.program.AttributeVariable.COLOR;
import static net.scriptgate.android.opengles.program.AttributeVariable.POSITION;
import static net.scriptgate.android.opengles.program.UniformVariable.MVP_MATRIX;


class Triangle {

    private final FloatBuffer vertices;
    private Point3D rotation = new Point3D();
    private Point3D position = new Point3D();

    Triangle(float[] verticesData) {
        vertices = allocateBuffer(verticesData);
    }

    void draw(Program program, ModelViewProjectionMatrix mvpMatrix, ViewMatrix viewMatrix, ModelMatrix modelMatrix, ProjectionMatrix projectionMatrix) {

        modelMatrix.setIdentity();
        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        program.pass(vertices).structuredAs(POSITION, COLOR);

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glDrawArrays(GL_TRIANGLES, 0, 3);

        glDisableVertexAttribArray(program.getHandle(POSITION));
        glDisableVertexAttribArray(program.getHandle(COLOR));
    }

    void setPosition(Point3D position) {
        this.position = position;
    }

    void setRotationX(float rotation) {
        this.rotation = this.rotation.x(rotation);
    }

    void setRotationY(float rotation) {
        this.rotation = this.rotation.y(rotation);
    }

    void setRotationZ(float rotation) {
        this.rotation = this.rotation.z(rotation);
    }
}
