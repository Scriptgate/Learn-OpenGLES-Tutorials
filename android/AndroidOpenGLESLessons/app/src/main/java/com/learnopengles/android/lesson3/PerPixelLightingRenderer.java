package com.learnopengles.android.lesson3;

import com.learnopengles.android.lesson2.LightingRenderer;

public class PerPixelLightingRenderer extends LightingRenderer {
    @Override
    protected String getVertexShader() {
        // Define our per-pixel lighting shader.
        return "lesson_three_vertex_shader";
    }

    @Override
    protected String getFragmentShader() {
        // Define our per-pixel shader
        return "lesson_three_fragment_shader";
    }
}
