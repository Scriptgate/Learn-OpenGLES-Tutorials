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
        CubeDataType cubeDataType = CubeDataType.TEXTURE_COORDINATE;
        int handle = program.getHandle(AttributeVariable.TEXTURE_COORDINATE);

        apply(cube, cubeDataType, handle);
    }
}
