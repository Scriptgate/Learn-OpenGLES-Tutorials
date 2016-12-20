package com.learnopengles.android.lesson5;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.AbstractCube;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;

public class Cube extends AbstractCube {

    public Cube(CubeDataCollection cubeData, Point3D point) {
        super(cubeData, point);
    }

    public void passData(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);

        passMVPMatrix(program, mvpMatrix);
    }
}
