package com.learnopengles.android.lesson2.renderer;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class TextureCubeRenderer implements CubeRenderer {

    private final Program program;

    public TextureCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        // Set the active texture unit to texture unit 0.
        cube.drawTexture(program);
    }
}
