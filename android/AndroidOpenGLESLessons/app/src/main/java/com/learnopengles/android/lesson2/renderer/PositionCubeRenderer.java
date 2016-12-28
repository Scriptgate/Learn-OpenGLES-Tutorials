package com.learnopengles.android.lesson2.renderer;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class PositionCubeRenderer implements CubeRenderer {

    private Program program;

    public PositionCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        cube.passPositionData(program);
    }
}
