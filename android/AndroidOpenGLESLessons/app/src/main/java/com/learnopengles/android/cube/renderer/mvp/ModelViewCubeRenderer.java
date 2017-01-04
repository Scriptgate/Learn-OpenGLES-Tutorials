package com.learnopengles.android.cube.renderer.mvp;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;
import static com.learnopengles.android.program.UniformVariable.MV_MATRIX;

public class ModelViewCubeRenderer implements RendererLink<Cube> {

    private ModelViewProjectionMatrix mvpMatrix;
    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix;
    private Program program;

    public ModelViewCubeRenderer(ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Program program) {
        this.mvpMatrix = mvpMatrix;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        mvpMatrix.multiply(modelMatrix, viewMatrix);
        mvpMatrix.passTo(program.getHandle(MV_MATRIX));

        mvpMatrix.multiply(projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));
    }
}
