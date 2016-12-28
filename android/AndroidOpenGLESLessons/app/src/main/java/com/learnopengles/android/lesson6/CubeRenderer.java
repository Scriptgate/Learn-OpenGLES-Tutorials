package com.learnopengles.android.lesson6;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class CubeRenderer {

    public static void drawCube(Cube cube, Program program, int textureDataHandle, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light light, float[] temporaryMatrix) {

        // Set the active texture unit to texture unit 0.
        cube.drawTexture(textureDataHandle, program);

        cube.passPositionData(program);
        cube.passNormalData(program);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        mvpMatrix.multiply(modelMatrix, viewMatrix);

        cube.passMVMatrix(program, mvpMatrix);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix, temporaryMatrix);

        cube.passMVPMatrix(program, mvpMatrix);

        // Pass in the light position in eye space.
        cube.passLightTo(program, light);

        cube.drawArrays();
    }
}
