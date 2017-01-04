package com.learnopengles.android.renderer;


import com.learnopengles.android.common.Light;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttrib3fv;
import static com.learnopengles.android.program.AttributeVariable.POSITION;

public class LightPositionRenderer implements RendererLink<Light> {

    private final Program program;

    public LightPositionRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Light light) {
        int handle = program.getHandle(POSITION);
        glDisableVertexAttribArray(handle);
        glVertexAttrib3fv(handle, light.getPositionInModelSpace(), 0);
    }
}
