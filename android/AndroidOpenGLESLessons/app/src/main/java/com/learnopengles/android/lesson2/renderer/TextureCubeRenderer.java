package com.learnopengles.android.lesson2.renderer;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class TextureCubeRenderer implements CubeRenderer {

    private final Program program;
    private final int textureDataHandle;

    public TextureCubeRenderer(Program program, int textureDataHandle) {
        this.program = program;
        this.textureDataHandle = textureDataHandle;
    }

    @Override
    public void apply(Cube cube) {
        // Set the active texture unit to texture unit 0.
        cube.drawTexture(textureDataHandle, program);
    }
}
