package com.learnopengles.android.lesson2;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
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
    private Light light;

    CubeRenderer(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix, Light light) {
        this.program = program;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mvpMatrix = mvpMatrix;
        this.light = light;
    }

    void useForRendering() {
        program.useForRendering();
    }

    void draw(Cube cube) {

        cube.apply(modelMatrix);

        passCubeDataToAttribute(cube, AttributeVariable.POSITION);
        passCubeDataToAttribute(cube, AttributeVariable.COLOR);
        passCubeDataToAttribute(cube, AttributeVariable.NORMAL);

        mvpMatrix.multiply(modelMatrix, viewMatrix);
        mvpMatrix.passTo(program.getHandle(MV_MATRIX));
        mvpMatrix.multiply(projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glUniform3fv(program.getHandle(LIGHT_POSITION), 1, light.getPositionInEyeSpace(), 0);

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    private void passCubeDataToAttribute(Cube cube, AttributeVariable attributeVariable) {
        int handle = program.getHandle(attributeVariable);

        FloatBuffer data = cube.getData(attributeVariable);
        data.position(0);
        glVertexAttribPointer(handle, attributeVariable.getSize(), GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }

}