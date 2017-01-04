package com.learnopengles.android.cube.renderer;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import static com.learnopengles.android.program.UniformVariable.LIGHT_POSITION;

public class LightCubeRenderer implements RendererLink<Cube> {


    private Light light;
    private Program program;

    public LightCubeRenderer(Light light, Program program) {
        this.light = light;
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        // Pass in the light position in eye space.
        light.passTo(program.getHandle(LIGHT_POSITION));
    }
}
