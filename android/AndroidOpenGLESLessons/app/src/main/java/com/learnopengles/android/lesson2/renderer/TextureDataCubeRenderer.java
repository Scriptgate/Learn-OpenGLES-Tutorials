package com.learnopengles.android.lesson2.renderer;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class TextureDataCubeRenderer implements CubeRenderer {

    private Program program;

    public TextureDataCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        cube.passTextureData(program);
    }
}
