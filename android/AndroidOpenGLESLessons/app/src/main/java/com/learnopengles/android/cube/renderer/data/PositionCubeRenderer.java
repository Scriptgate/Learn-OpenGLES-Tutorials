package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class PositionCubeRenderer extends CubeDataRenderer {

    public PositionCubeRenderer(Program program) {
        super(CubeDataType.POSITION, program, AttributeVariable.POSITION);
    }
}
