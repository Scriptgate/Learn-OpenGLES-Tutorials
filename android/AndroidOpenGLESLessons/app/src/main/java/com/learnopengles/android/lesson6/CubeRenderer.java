package com.learnopengles.android.lesson6;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeData;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.program.UniformVariable.*;

class CubeRenderer {

    private final Program program;
    private final ModelMatrix modelMatrix;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final ModelViewProjectionMatrix mvpMatrix;
    private final float[] accumulatedRotation;
    private final ModelMatrix currentRotation;
    private final float[] temporaryMatrix;
    private final Light light;

    CubeRenderer(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix, float[] accumulatedRotation, ModelMatrix currentRotation, float[] temporaryMatrix, Light light) {
        this.program = program;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mvpMatrix = mvpMatrix;
        this.accumulatedRotation = accumulatedRotation;
        this.currentRotation = currentRotation;
        this.temporaryMatrix = temporaryMatrix;
        this.light = light;
    }

    void useForRendering() {
        program.useForRendering();
    }

    void draw(Cube cube) {

        cube.apply(modelMatrix);

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        //TODO: accumulatedRotation = currentRotation * accumulatedRotation
        currentRotation.multiplyWithMatrixAndStore(accumulatedRotation, temporaryMatrix, accumulatedRotation);
        // Rotate the cube taking the overall rotation into account.
        modelMatrix.multiplyWithMatrixAndStore(accumulatedRotation, temporaryMatrix);

        passCubeDataToAttribute(cube, CubeDataType.POSITION, AttributeVariable.POSITION);
        passCubeDataToAttribute(cube, CubeDataType.NORMAL, AttributeVariable.NORMAL);
        passCubeDataToAttribute(cube, CubeDataType.TEXTURE_COORDINATE, AttributeVariable.TEXTURE_COORDINATE);

        bindTexture(cube.getTexture());

        mvpMatrix.multiply(modelMatrix, viewMatrix);
        mvpMatrix.passTo(program.getHandle(MV_MATRIX));
        mvpMatrix.multiply(projectionMatrix, temporaryMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glUniform3fv(program.getHandle(LIGHT_POSITION), 1, light.getPositionInEyeSpace(), 0);

        glDrawArrays(GL_TRIANGLES, 0, 36);

    }

    private void bindTexture(int texture) {
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, texture);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(program.getHandle(TEXTURE), 0);
    }

    private void passCubeDataToAttribute(Cube cube, CubeDataType cubeDataType, AttributeVariable attributeVariable) {
        CubeData cubeData = cube.getCubeData(cubeDataType);
        int handle = program.getHandle(attributeVariable);

        FloatBuffer data = cubeData.getData();
        data.position(0);
        glVertexAttribPointer(handle, cubeData.getDataSize(), GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }

}
