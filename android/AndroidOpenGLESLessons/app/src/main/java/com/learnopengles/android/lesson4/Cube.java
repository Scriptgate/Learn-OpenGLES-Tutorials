package com.learnopengles.android.lesson4;

import com.learnopengles.android.common.Light;
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

    @Override
    public void passData(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        passNormalData(program);
        passTextureData(program);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        mvpMatrix.multiply(modelMatrix, viewMatrix);
        passMVMatrix(program, mvpMatrix);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix);
        passMVPMatrix(program, mvpMatrix);
    }

    @Override
    public void passData(Program program, Light light) {
        passLightTo(program, light);
    }

}
