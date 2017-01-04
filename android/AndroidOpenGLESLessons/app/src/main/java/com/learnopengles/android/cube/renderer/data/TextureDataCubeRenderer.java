package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class TextureDataCubeRenderer extends CubeDataRenderer {

    private Program program;

    public TextureDataCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        apply(cube, CubeDataType.TEXTURE_COORDINATE, program, AttributeVariable.TEXTURE_COORDINATE);
    }
}
