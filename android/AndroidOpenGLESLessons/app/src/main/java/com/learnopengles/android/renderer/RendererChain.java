package com.learnopengles.android.renderer;

import java.util.Collections;
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

    public <S extends T> RendererChain<S> andThen(final RendererLink<S> rendererLink) {
        return new RendererChain<S>(Collections.<RendererLink<S>>emptyList()) {
            @Override
            public void draw(S s) {
                RendererChain.this.draw(s);
                rendererLink.apply(s);
            }
        };
    }
}
