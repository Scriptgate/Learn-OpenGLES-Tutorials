package com.learnopengles.android.renderer.light;


import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.Program.createProgram;
import static java.util.Collections.singletonList;

public class LightRendererFactory {

    public static LightRenderer createLightRenderer(ModelViewProjectionMatrix mvpMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        Program pointProgram = createProgram("point_vertex_shader", "point_fragment_shader", singletonList(POSITION));
        return new LightRenderer(pointProgram, viewMatrix, projectionMatrix, mvpMatrix);

    }

}
