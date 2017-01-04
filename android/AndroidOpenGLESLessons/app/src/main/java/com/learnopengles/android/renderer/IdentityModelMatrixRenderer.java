package com.learnopengles.android.renderer;


import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.program.Program;

public class IdentityModelMatrixRenderer<T> implements RendererLink<T> {

    private final ModelMatrix modelMatrix;

    public IdentityModelMatrixRenderer(ModelMatrix modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    @Override
    public void apply(Program program, T t) {
        modelMatrix.setIdentity();
    }
}
