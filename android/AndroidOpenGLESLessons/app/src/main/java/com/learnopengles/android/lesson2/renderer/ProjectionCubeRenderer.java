package com.learnopengles.android.lesson2.renderer;

import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

public class ProjectionCubeRenderer implements CubeRenderer {

    private ModelViewProjectionMatrix mvpMatrix;
    private ProjectionMatrix projectionMatrix;
    private Program program;

    public ProjectionCubeRenderer(ModelViewProjectionMatrix mvpMatrix, ProjectionMatrix projectionMatrix, Program program) {
        this.mvpMatrix = mvpMatrix;
        this.projectionMatrix = projectionMatrix;
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix);
        cube.passMVPMatrix(program, mvpMatrix);
    }
}
