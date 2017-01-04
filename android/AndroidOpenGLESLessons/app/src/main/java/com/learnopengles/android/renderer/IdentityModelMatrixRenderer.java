package com.learnopengles.android.renderer;


import com.learnopengles.android.component.ModelMatrix;

public class IdentityModelMatrixRenderer<T> implements RendererLink<T> {

    private final ModelMatrix modelMatrix;

    public IdentityModelMatrixRenderer(ModelMatrix modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    @Override
    public void apply(T t) {
        modelMatrix.setIdentity();
    }
}
