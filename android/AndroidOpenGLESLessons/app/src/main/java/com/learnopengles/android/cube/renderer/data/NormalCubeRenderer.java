package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class NormalCubeRenderer extends CubeDataRenderer {

    public NormalCubeRenderer(Program program) {
        super(CubeDataType.NORMAL, program, AttributeVariable.NORMAL);
    }
}
