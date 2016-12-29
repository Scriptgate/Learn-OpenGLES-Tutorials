package com.learnopengles.android.cube.renderer.mvp;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.renderer.CubeRenderer;
import com.learnopengles.android.program.Program;

import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;
import static com.learnopengles.android.program.UniformVariable.MV_MATRIX;

public class ProjectionThroughTemporaryMatrixCubeRenderer implements CubeRenderer {

    private ModelViewProjectionMatrix mvpMatrix;
    private ModelMatrix modelMatrix;
    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix;
    private Program program;
    private float[] temporaryMatrix;

    public ProjectionThroughTemporaryMatrixCubeRenderer(ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Program program, float[] temporaryMatrix) {
        this.mvpMatrix = mvpMatrix;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.program = program;
        this.temporaryMatrix = temporaryMatrix;
    }

    @Override
    public void apply(Cube cube) {
        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        mvpMatrix.multiply(modelMatrix, viewMatrix);
        // Pass in the modelview matrix.
        mvpMatrix.passTo(program.getHandle(MV_MATRIX));

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix, temporaryMatrix);
        // Pass in the combined matrix.
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));
    }
}
