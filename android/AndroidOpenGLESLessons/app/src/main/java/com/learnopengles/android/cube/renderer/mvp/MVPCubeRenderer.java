package com.learnopengles.android.cube.renderer.mvp;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.MVPRenderer;
import com.learnopengles.android.renderer.RendererLink;

public class MVPCubeRenderer extends MVPRenderer<Cube> implements RendererLink<Cube> {

    public MVPCubeRenderer(ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Program program) {
        super(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program);
    }
}
