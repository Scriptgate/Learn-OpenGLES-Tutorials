package com.learnopengles.android.lesson12;


import android.opengl.GLES20;

import net.scriptgate.android.common.Point3D;
import net.scriptgate.android.opengles.matrix.ModelMatrix;
import net.scriptgate.android.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ViewMatrix;
import net.scriptgate.android.opengles.program.AttributeVariable;
import net.scriptgate.android.opengles.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static net.scriptgate.android.nio.BufferHelper.allocateBuffer;
import static net.scriptgate.android.opengles.program.UniformVariable.MVP_MATRIX;

public class Square {

    static final int ELEMENTS_PER_FACE = 6;

    private FloatBuffer verticesBuffer;
    private FloatBuffer textureCoordinateBuffer;

    private Point3D position;
    private Point3D rotation;
    private int texture;
    private Point3D scale = new Point3D(1,1,1);



    public static Square createSquare(Point3D position, Point3D rotation, float[] verticesData, float[] textureData) {
        FloatBuffer verticesBuffer = allocateBuffer(verticesData);
        FloatBuffer textureCoordinateBuffer = allocateBuffer(textureData);
        return new Square(position, rotation, verticesBuffer, textureCoordinateBuffer);
    }

    private Square(Point3D position, Point3D rotation, FloatBuffer verticesBuffer, FloatBuffer textureCoordinateBuffer) {
        this.position = position;
        this.rotation = rotation;
        this.verticesBuffer = verticesBuffer;
        this.textureCoordinateBuffer = textureCoordinateBuffer;
    }

    public void draw(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix) {
        modelMatrix.setIdentity();
        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);
        modelMatrix.scale(scale);

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));


        program.bindTexture(texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GLES20.GL_NEAREST);

        program.pass(verticesBuffer).at(0).to(AttributeVariable.POSITION);

        program.pass(textureCoordinateBuffer).at(0).to(AttributeVariable.TEXTURE_COORDINATE);

        glDrawArrays(GL_TRIANGLES, 0, ELEMENTS_PER_FACE);
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }

    public void setScale(Point3D scale) {
        this.scale = scale;
    }

    public void translate(Point3D offset) {
        this.position = Point3D.addition(position, offset);
    }

    public Point3D getPosition() {
        return position;
    }

    void transform(float[] deltaRotationVector) {
        float x = position.x() + deltaRotationVector[1]/7.0f;

        float MINIMUM = -1.1f;
        float MAXIMUM = -0.4f;

        if(x < MINIMUM) {
            x = MINIMUM;
        } else if(x > MAXIMUM) {
            x = MAXIMUM;
        }
        position = position.x(x);
    }
}
