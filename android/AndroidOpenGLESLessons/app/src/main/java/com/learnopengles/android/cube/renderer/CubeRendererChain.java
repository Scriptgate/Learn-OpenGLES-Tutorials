package com.learnopengles.android.cube.renderer;


import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.renderer.RendererLink;

import java.util.List;

public class CubeRendererChain {

    private List<RendererLink<Cube>> rendererChain;

    public CubeRendererChain(List<RendererLink<Cube>> rendererChain) {
        this.rendererChain = rendererChain;
    }

    public void drawCube(Cube cube) {
        for (RendererLink<Cube> cubeRenderer : rendererChain) {
            cubeRenderer.apply(cube);
        }
        cube.drawArrays();
    }
}
