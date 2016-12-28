package com.learnopengles.android.lesson6;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.lesson2.renderer.CubeRenderer;
import com.learnopengles.android.lesson2.renderer.LightCubeRenderer;
import com.learnopengles.android.lesson2.renderer.ModelViewCubeRenderer;
import com.learnopengles.android.lesson2.renderer.NormalCubeRenderer;
import com.learnopengles.android.lesson2.renderer.PositionCubeRenderer;
import com.learnopengles.android.lesson2.renderer.ProjectionThroughTemporaryMatrixCubeRenderer;
import com.learnopengles.android.lesson2.renderer.TextureCubeRenderer;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

public class CubeRendererChain {

    public static void drawCube(Cube cube, Program program, int textureDataHandle, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light light, float[] temporaryMatrix) {
        List<CubeRenderer> renderChain = new ArrayList<>();

        renderChain.add(new PositionCubeRenderer(program));
        renderChain.add(new NormalCubeRenderer(program));
        renderChain.add(new TextureCubeRenderer(program, textureDataHandle));
        renderChain.add(new ModelViewCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, program));
        renderChain.add(new ProjectionThroughTemporaryMatrixCubeRenderer(mvpMatrix, projectionMatrix, program, temporaryMatrix));
        renderChain.add(new LightCubeRenderer(light, program));

        for (CubeRenderer cubeRenderer : renderChain) {
            cubeRenderer.apply(cube);
        }

        cube.drawArrays();
    }
}
