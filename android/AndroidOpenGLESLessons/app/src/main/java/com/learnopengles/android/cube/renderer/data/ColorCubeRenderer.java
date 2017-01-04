package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class ColorCubeRenderer extends CubeDataRenderer {

    public ColorCubeRenderer(Program program) {
        super(CubeDataType.COLOR, program, AttributeVariable.COLOR);
    }
}
