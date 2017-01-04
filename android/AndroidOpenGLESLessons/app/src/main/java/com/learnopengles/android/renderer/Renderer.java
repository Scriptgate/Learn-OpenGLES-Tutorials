package com.learnopengles.android.renderer;

import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

public class Renderer<T> {

    private Program program;
    private List<RendererLink<T>> rendererChain;

    public Renderer(Program program) {
        this.program = program;
        this.rendererChain = new ArrayList<>();
    }

    public Renderer(Program program, List<RendererLink<T>> rendererChain) {
        this.program = program;
        this.rendererChain = rendererChain;
    }

    public void draw(T t) {
        for (RendererLink<T> rendererLink : rendererChain) {
            rendererLink.apply(program, t);
        }
    }

    public <S extends T> Renderer<S> andThen(final RendererLink<S> rendererLink) {
        return new Renderer<S>(program) {
            @Override
            public void draw(S s) {
                Renderer.this.draw(s);
                rendererLink.apply(program, s);
            }
        };
    }
}
