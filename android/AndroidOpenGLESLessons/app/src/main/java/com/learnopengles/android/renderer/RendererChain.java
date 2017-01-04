package com.learnopengles.android.renderer;

import java.util.List;

public class RendererChain<T> {

    private List<RendererLink<T>> rendererChain;

    public RendererChain(List<RendererLink<T>> rendererChain) {
        this.rendererChain = rendererChain;
    }

    public void draw(T t) {
        for (RendererLink<T> rendererLink : rendererChain) {
            rendererLink.apply(t);
        }
    }
}
