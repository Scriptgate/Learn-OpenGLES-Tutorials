package com.learnopengles.android.cube.renderer;


import com.learnopengles.android.cube.Cube;

import java.util.List;

public class CubeRendererChain {

    private List<CubeRenderer> rendererChain;

    public CubeRendererChain(List<CubeRenderer> rendererChain) {
        this.rendererChain = rendererChain;
    }

    public void drawCube(Cube cube) {
        for (CubeRenderer cubeRenderer : rendererChain) {
            cubeRenderer.apply(cube);
        }
        cube.drawArrays();
    }
}
