package com.learnopengles.android.renderer;


import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;

public class MVPWithProjectionThroughTemporaryMatrixRenderer<T> implements RendererLink<T> {

    private ModelViewProjectionMatrix mvpMatrix;
    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix;
    private Program program;
    private float[] temporaryMatrix;

    public MVPWithProjectionThroughTemporaryMatrixRenderer(ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Program program, float[] temporaryMatrix) {
        this.mvpMatrix = mvpMatrix;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.program = program;
        this.temporaryMatrix = temporaryMatrix;
    }

    @Override
    public void apply(T t) {
        mvpMatrix.multiply(modelMatrix, viewMatrix);
        mvpMatrix.multiply(projectionMatrix, temporaryMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));
    }

}
