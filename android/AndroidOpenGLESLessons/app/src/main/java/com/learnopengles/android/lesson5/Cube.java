package com.learnopengles.android.lesson5;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;

import static android.opengl.GLES20.*;

public class Cube {

    private CubeData cubeData;
    private Point3D position = new Point3D();
    private Point3D rotation = new Point3D();

    public Cube(CubeData cubeData, Point3D point) {
        this.cubeData = cubeData;
        this.position = point;
    }

    public void drawCube(int programHandle, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        int mvpMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        int positionHandle = glGetAttribLocation(programHandle, "a_Position");
        int colorHandle = glGetAttribLocation(programHandle, "a_Color");

        // Pass in the position information
        cubeData.passPositionTo(positionHandle);

        // Pass in the color information
        cubeData.passColorTo(colorHandle);

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);

        // Pass in the combined matrix.
        mvpMatrix.passTo(mvpMatrixHandle);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void setRotationX(float rotation) {
        this.rotation.x = rotation;
    }

    public void setRotationY(float rotation) {
        this.rotation.y = rotation;
    }

    public void setRotationZ(float rotation) {
        this.rotation.z = rotation;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }
}
