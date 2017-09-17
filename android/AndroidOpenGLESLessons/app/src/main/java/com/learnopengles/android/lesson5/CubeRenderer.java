package com.learnopengles.android.lesson5;

import net.scriptgate.android.opengles.matrix.ModelMatrix;
import net.scriptgate.android.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ViewMatrix;
import net.scriptgate.android.opengles.cube.Cube;
import net.scriptgate.android.opengles.program.Program;

import static android.opengl.GLES20.*;
import static net.scriptgate.android.opengles.program.AttributeVariable.*;
import static net.scriptgate.android.opengles.program.UniformVariable.MVP_MATRIX;

class CubeRenderer {

    private final Program program;
    private final ModelMatrix modelMatrix;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final ModelViewProjectionMatrix mvpMatrix;

    CubeRenderer(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix) {
        this.program = program;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mvpMatrix = mvpMatrix;
    }

    void draw(Cube cube) {
        cube.apply(modelMatrix);

        program.pass(cube.getData(POSITION)).to(POSITION);
        program.pass(cube.getData(COLOR)).to(COLOR);

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}
