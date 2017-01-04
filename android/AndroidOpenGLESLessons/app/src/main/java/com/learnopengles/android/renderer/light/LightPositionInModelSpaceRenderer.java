package com.learnopengles.android.renderer.light;


import com.learnopengles.android.common.Light;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttrib3fv;
import static com.learnopengles.android.program.AttributeVariable.POSITION;

public class LightPositionInModelSpaceRenderer implements RendererLink<Light> {

    @Override
    public void apply(Program program, Light light) {
        int handle = program.getHandle(POSITION);
        glDisableVertexAttribArray(handle);
        glVertexAttrib3fv(handle, light.getPositionInModelSpace(), 0);
    }
}
