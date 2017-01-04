package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class TextureDataCubeRenderer extends CubeDataRenderer {

    public TextureDataCubeRenderer(Program program) {
        super(CubeDataType.TEXTURE_COORDINATE, program, AttributeVariable.TEXTURE_COORDINATE);
    }
}
