package com.learnopengles.android.renderer.light;


import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.DrawArraysRenderer;
import com.learnopengles.android.renderer.MVPRenderer;
import com.learnopengles.android.renderer.Renderer;

import static android.opengl.GLES20.GL_POINTS;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.Program.createProgram;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class LightRendererFactory {

    public static Renderer<Light> createLightRenderer(Light light, ModelViewProjectionMatrix mvpMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        Program pointProgram = createProgram("point_vertex_shader", "point_fragment_shader", singletonList(POSITION));
        return new Renderer<>(pointProgram,
                asList(
                        new LightPositionInModelSpaceRenderer(),
                        new MVPRenderer<Light>(mvpMatrix, light.getModelMatrix(), viewMatrix, projectionMatrix),
                        new DrawArraysRenderer<Light>(GL_POINTS, 1)
                )
        );
    }

}
