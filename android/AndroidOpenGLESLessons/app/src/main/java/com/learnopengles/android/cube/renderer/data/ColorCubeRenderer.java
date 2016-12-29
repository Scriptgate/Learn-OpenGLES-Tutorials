package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.renderer.CubeRenderer;
import com.learnopengles.android.program.Program;

import static com.learnopengles.android.program.AttributeVariable.COLOR;

public class ColorCubeRenderer implements CubeRenderer {

    private Program program;

    public ColorCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        cube.passColorData(program.getHandle(COLOR));
    }
}
