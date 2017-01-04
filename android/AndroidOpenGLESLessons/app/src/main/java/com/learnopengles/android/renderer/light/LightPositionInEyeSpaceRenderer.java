package com.learnopengles.android.renderer.light;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import static android.opengl.GLES20.glUniform3fv;
import static com.learnopengles.android.program.UniformVariable.LIGHT_POSITION;

public class LightPositionInEyeSpaceRenderer<T> implements RendererLink<T> {


    private Light light;

    public LightPositionInEyeSpaceRenderer(Light light) {
        this.light = light;
    }

    @Override
    public void apply(Program program, T t) {
        glUniform3fv(program.getHandle(LIGHT_POSITION), 1, light.getPositionInEyeSpace(), 0);
    }
}
