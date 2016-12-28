package com.learnopengles.android.lesson5;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class CubeRenderer {

    public static void drawCube(Cube cube, Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        cube.apply(modelMatrix);

        cube.passPositionData(program);
        cube.passColorData(program);

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);

        cube.passMVPMatrix(program, mvpMatrix);

        cube.drawArrays();
    }
}
