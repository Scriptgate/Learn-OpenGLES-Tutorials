package com.learnopengles.android.lesson2.renderer;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class LightCubeRenderer implements CubeRenderer {


    private Light light;
    private Program program;

    public LightCubeRenderer(Light light, Program program) {
        this.light = light;
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        cube.passLightTo(program, light);
    }
}
