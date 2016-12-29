package com.learnopengles.android.lesson6;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.renderer.CubeRenderer;
import com.learnopengles.android.cube.renderer.LightCubeRenderer;
import com.learnopengles.android.cube.renderer.ModelViewCubeRenderer;
import com.learnopengles.android.cube.renderer.NormalCubeRenderer;
import com.learnopengles.android.cube.renderer.PositionCubeRenderer;
import com.learnopengles.android.cube.renderer.ProjectionThroughTemporaryMatrixCubeRenderer;
import com.learnopengles.android.cube.renderer.TextureCubeRenderer;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

public class CubeRendererChain {

    private List<CubeRenderer> renderChain = new ArrayList<>();

    public CubeRendererChain(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light light, float[] temporaryMatrix) {
        renderChain.add(new PositionCubeRenderer(program));
        renderChain.add(new NormalCubeRenderer(program));
        renderChain.add(new TextureCubeRenderer(program));
        renderChain.add(new ModelViewCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, program));
        renderChain.add(new ProjectionThroughTemporaryMatrixCubeRenderer(mvpMatrix, projectionMatrix, program, temporaryMatrix));
        renderChain.add(new LightCubeRenderer(light, program));
    }

    public void drawCube(Cube cube) {
        for (CubeRenderer cubeRenderer : renderChain) {
            cubeRenderer.apply(cube);
        }

        cube.drawArrays();
    }
}
