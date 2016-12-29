package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.cube.renderer.CubeRenderer;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class ColorCubeRenderer implements CubeRenderer {

    private Program program;

    public ColorCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        cube.passTo(CubeDataType.COLOR, program.getHandle(AttributeVariable.COLOR));
    }
}
