package com.learnopengles.android.lesson2;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.renderer.ColorCubeRenderer;
import com.learnopengles.android.cube.renderer.CubeRenderer;
import com.learnopengles.android.cube.renderer.LightCubeRenderer;
import com.learnopengles.android.cube.renderer.ModelMatrixCubeRenderer;
import com.learnopengles.android.cube.renderer.ModelViewCubeRenderer;
import com.learnopengles.android.cube.renderer.NormalCubeRenderer;
import com.learnopengles.android.cube.renderer.PositionCubeRenderer;
import com.learnopengles.android.cube.renderer.ProjectionCubeRenderer;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

public class CubeRendererChain {

    public static void drawCube(Cube cube, Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light light) {
        List<CubeRenderer> renderChain = new ArrayList<>();

        renderChain.add(new ModelMatrixCubeRenderer(modelMatrix));
        renderChain.add(new PositionCubeRenderer(program));
        renderChain.add(new ColorCubeRenderer(program));
        renderChain.add(new NormalCubeRenderer(program));
        renderChain.add(new ModelViewCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, program));
        renderChain.add(new ProjectionCubeRenderer(mvpMatrix, projectionMatrix, program));
        renderChain.add(new LightCubeRenderer(light, program));

        for (CubeRenderer cubeRenderer : renderChain) {
            cubeRenderer.apply(cube);
        }

        cube.drawArrays();
    }
}
