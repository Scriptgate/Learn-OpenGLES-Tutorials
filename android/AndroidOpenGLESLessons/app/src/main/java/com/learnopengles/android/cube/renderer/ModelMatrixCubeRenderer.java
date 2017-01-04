package com.learnopengles.android.cube.renderer;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

public class ModelMatrixCubeRenderer implements RendererLink<Cube> {

    private ModelMatrix modelMatrix;

    public ModelMatrixCubeRenderer(ModelMatrix modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    @Override
    public void apply(Program program, Cube cube) {
        cube.apply(modelMatrix);
    }
}
