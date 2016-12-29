package com.learnopengles.android.cube.renderer;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

import static com.learnopengles.android.program.AttributeVariable.NORMAL;

public class NormalCubeRenderer implements CubeRenderer {

    private Program program;

    public NormalCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        cube.passNormalData(program.getHandle(NORMAL));
    }
}
