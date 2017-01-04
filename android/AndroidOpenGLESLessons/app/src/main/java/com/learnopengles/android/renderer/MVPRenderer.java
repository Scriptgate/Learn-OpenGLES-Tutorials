package com.learnopengles.android.renderer;


import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;

public class MVPRenderer<T> implements RendererLink<T> {

    private final ModelViewProjectionMatrix mvpMatrix;
    private final ModelMatrix modelMatrix;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final Program program;

    public MVPRenderer(ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Program program) {
        this.mvpMatrix = mvpMatrix;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.program = program;
    }

    @Override
    public void apply(T t) {
        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        // Pass in the combined matrix.
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));
    }
}
