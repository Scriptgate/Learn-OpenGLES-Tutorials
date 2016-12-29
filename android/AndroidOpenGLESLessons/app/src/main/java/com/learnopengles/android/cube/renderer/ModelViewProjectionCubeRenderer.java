package com.learnopengles.android.cube.renderer;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class ModelViewProjectionCubeRenderer implements CubeRenderer {

    private final ModelViewProjectionMatrix mvpMatrix;
    private final ModelMatrix modelMatrix;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final Program program;

    public ModelViewProjectionCubeRenderer(ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Program program) {

        this.mvpMatrix = mvpMatrix;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        cube.passMVPMatrix(program, mvpMatrix);
    }
}
