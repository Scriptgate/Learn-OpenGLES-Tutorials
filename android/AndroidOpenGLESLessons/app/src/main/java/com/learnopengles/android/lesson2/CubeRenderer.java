package com.learnopengles.android.lesson2;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.program.AttributeVariable.*;
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

        program.pass(cube.getData(POSITION)).to(POSITION);
        program.pass(cube.getData(COLOR)).to(COLOR);
        program.pass(cube.getData(NORMAL)).to(NORMAL);

        mvpMatrix.multiply(modelMatrix, viewMatrix);
        mvpMatrix.passTo(program.getHandle(MV_MATRIX));
        mvpMatrix.multiply(projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glUniform3fv(program.getHandle(LIGHT_POSITION), 1, light.getPositionInEyeSpace(), 0);

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}