package com.learnopengles.android.cube.renderer;

import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class ProjectionThroughTemporaryMatrixCubeRenderer implements CubeRenderer {

    private ModelViewProjectionMatrix mvpMatrix;
    private ProjectionMatrix projectionMatrix;
    private Program program;
    private float[] temporaryMatrix;

    public ProjectionThroughTemporaryMatrixCubeRenderer(ModelViewProjectionMatrix mvpMatrix, ProjectionMatrix projectionMatrix, Program program, float[] temporaryMatrix) {
        this.mvpMatrix = mvpMatrix;
        this.projectionMatrix = projectionMatrix;
        this.program = program;
        this.temporaryMatrix = temporaryMatrix;
    }

    @Override
    public void apply(Cube cube) {
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix, temporaryMatrix);
        cube.passMVPMatrix(program, mvpMatrix);
    }
}
