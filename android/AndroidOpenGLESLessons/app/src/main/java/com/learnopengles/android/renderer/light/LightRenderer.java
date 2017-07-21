package com.learnopengles.android.renderer.light;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;

public class LightRenderer {
    private final Program program;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final ModelViewProjectionMatrix mvpMatrix;

    LightRenderer(Program program, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix) {
        this.program = program;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mvpMatrix = mvpMatrix;
    }

    public void draw(Light light) {
        int positionHandle = program.getHandle(POSITION);
        glDisableVertexAttribArray(positionHandle);
        glVertexAttrib3fv(positionHandle, light.getPositionInModelSpace(), 0);

        mvpMatrix.multiply(light.getModelMatrix(), viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glDrawArrays(GL_POINTS, 0, 1);
    }

    public void useForRendering() {
        program.useForRendering();
    }
}
