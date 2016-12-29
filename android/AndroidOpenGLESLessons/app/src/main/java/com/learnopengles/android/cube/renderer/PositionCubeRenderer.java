package com.learnopengles.android.cube.renderer;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

import static com.learnopengles.android.program.AttributeVariable.POSITION;

public class PositionCubeRenderer implements CubeRenderer {

    private Program program;

    public PositionCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        cube.passPositionData(program.getHandle(POSITION));
    }
}
