package com.learnopengles.android.lesson5;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.lesson2.renderer.ColorCubeRenderer;
import com.learnopengles.android.lesson2.renderer.CubeRenderer;
import com.learnopengles.android.lesson2.renderer.ModelMatrixCubeRenderer;
import com.learnopengles.android.lesson2.renderer.ModelViewProjectionCubeRenderer;
import com.learnopengles.android.lesson2.renderer.PositionCubeRenderer;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

public class CubeRendererChain {



    public static void drawCube(Cube cube, Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        List<CubeRenderer> renderChain = new ArrayList<>();

        renderChain.add(new ModelMatrixCubeRenderer(modelMatrix));
        renderChain.add(new PositionCubeRenderer(program));
        renderChain.add(new ColorCubeRenderer(program));
        renderChain.add(new ModelViewProjectionCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program));

        for (CubeRenderer cubeRenderer : renderChain) {
            cubeRenderer.apply(cube);
        }

        cube.drawArrays();
    }
}
