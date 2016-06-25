package com.learnopengles.android.lesson3;

import com.learnopengles.android.lesson2.LessonTwoRenderer;

import static com.learnopengles.android.common.RawResourceReader.readShaderFileFromResource;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class LessonThreeRenderer extends LessonTwoRenderer {
    @Override
    protected String getVertexShader() {
        // Define our per-pixel lighting shader.
        return readShaderFileFromResource("lesson_three_vertex_shader");
    }

    @Override
    protected String getFragmentShader() {
        // Define our per-pixel shader
        return readShaderFileFromResource("lesson_three_fragment_shader");
    }
}
