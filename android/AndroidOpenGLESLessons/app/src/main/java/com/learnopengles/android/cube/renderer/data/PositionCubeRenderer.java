package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class PositionCubeRenderer extends CubeDataRenderer {

    private Program program;

    public PositionCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        apply(cube, CubeDataType.POSITION, program, AttributeVariable.POSITION);
    }
}
