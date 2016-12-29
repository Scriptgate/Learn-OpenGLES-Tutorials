package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.renderer.CubeRenderer;
import com.learnopengles.android.program.Program;

import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;

public class TextureDataCubeRenderer implements CubeRenderer {

    private Program program;

    public TextureDataCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        cube.passTextureData(program.getHandle(TEXTURE_COORDINATE));
    }
}
