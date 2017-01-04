package com.learnopengles.android.renderer.light;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import static android.opengl.GLES20.glUniform3fv;
import static com.learnopengles.android.program.UniformVariable.LIGHT_POSITION;

public class LightPositionInEyeSpaceRenderer<T> implements RendererLink<T> {


    private Light light;
    private Program program;

    public LightPositionInEyeSpaceRenderer(Light light, Program program) {
        this.light = light;
        this.program = program;
    }

    @Override
    public void apply(T t) {
        glUniform3fv(program.getHandle(LIGHT_POSITION), 1, light.getPositionInEyeSpace(), 0);
    }
}
