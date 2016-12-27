package com.learnopengles.android.lesson6;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.UniformVariable.*;

public class Cube  {

    private CubeDataCollection cubeData;

    public Cube(CubeDataCollection cubeData) {
        this.cubeData = cubeData;
    }

    public void drawCube(Program program,  int textureDataHandle, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light light, float[] temporaryMatrix) {
        // Set the active texture unit to texture unit 0.
        drawTexture(textureDataHandle, program);

        passPositionData(program);
        passNormalData(program);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        mvpMatrix.multiply(modelMatrix, viewMatrix);

        passMVMatrix(program, mvpMatrix);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix, temporaryMatrix);

        passMVPMatrix(program, mvpMatrix);

        // Pass in the light position in eye space.
        passLightTo(program, light);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    private void drawTexture(int textureHandle, Program program) {
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        int textureUniformHandle = program.getHandle(TEXTURE);
        glUniform1i(textureUniformHandle, 0);

        passTextureData(program);
    }

    private void passPositionData(Program program) {
        // Pass in the position information
        int positionHandle = program.getHandle(POSITION);
        cubeData.passPositionTo(positionHandle);
    }

    private void passNormalData(Program program) {
        // Pass in the normal information
        int normalHandle = program.getHandle(NORMAL);
        cubeData.passNormalTo(normalHandle);
    }

    private void passTextureData(Program program) {
        // Pass in the texture coordinate information
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);
        cubeData.passTextureTo(textureCoordinateHandle);
    }

    private void passMVPMatrix(Program program, ModelViewProjectionMatrix mvpMatrix) {
        // Pass in the combined matrix.
        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        mvpMatrix.passTo(mvpMatrixHandle);
    }

    private void passMVMatrix(Program program, ModelViewProjectionMatrix mvpMatrix) {
        // Pass in the modelview matrix.
        int mvMatrixHandle = program.getHandle(MV_MATRIX);
        mvpMatrix.passTo(mvMatrixHandle);
    }

    private void passLightTo(Program program, Light light) {
        // Pass in the light position in eye space.
        int lightPosHandle = program.getHandle(LIGHT_POSITION);
        light.passTo(lightPosHandle);
    }

}
