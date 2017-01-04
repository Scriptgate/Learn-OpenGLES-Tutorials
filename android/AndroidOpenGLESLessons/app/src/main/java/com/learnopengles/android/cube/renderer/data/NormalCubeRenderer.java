package com.learnopengles.android.cube.renderer.data;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class NormalCubeRenderer extends CubeDataRenderer {

    private Program program;

    public NormalCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        CubeDataType cubeDataType = CubeDataType.NORMAL;
        int handle = program.getHandle(AttributeVariable.NORMAL);

        apply(cube, cubeDataType, handle);
    }
}
