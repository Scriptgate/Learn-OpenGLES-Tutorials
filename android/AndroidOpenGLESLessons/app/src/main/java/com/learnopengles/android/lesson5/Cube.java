package com.learnopengles.android.lesson5;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;

public class Cube {

    private CubeDataCollection cubeData;
    private Point3D position = new Point3D();
    private Point3D rotation = new Point3D();

    public Cube(CubeDataCollection cubeData, Point3D point) {
        this.cubeData = cubeData;
        this.position = point;
    }

    public void drawCube(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        int positionHandle = program.getHandle(POSITION);
        int colorHandle = program.getHandle(COLOR);

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
