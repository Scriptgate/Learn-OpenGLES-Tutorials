package com.learnopengles.android.lesson5;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.*;

public class CubeRenderer {

    public static void drawCube(Cube cube, Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        cube.apply(modelMatrix);
        cube.passPositionData(program);
        cube.passColorData(program);

        passData(cube, program, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix);

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    private static void passData(Cube cube, Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);

        cube.passMVPMatrix(program, mvpMatrix);
    }
}
