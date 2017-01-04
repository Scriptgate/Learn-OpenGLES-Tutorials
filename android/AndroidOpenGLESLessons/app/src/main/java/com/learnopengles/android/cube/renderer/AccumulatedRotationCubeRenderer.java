package com.learnopengles.android.cube.renderer;


import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererLink;

public class AccumulatedRotationCubeRenderer implements RendererLink<Cube> {

    private final float[] accumulatedRotation;
    private final ModelMatrix currentRotation;
    private float[] temporaryMatrix;
    private ModelMatrix modelMatrix;

    public AccumulatedRotationCubeRenderer(float[] accumulatedRotation, ModelMatrix currentRotation, float[] temporaryMatrix, ModelMatrix modelMatrix) {
        this.accumulatedRotation = accumulatedRotation;
        this.currentRotation = currentRotation;
        this.temporaryMatrix = temporaryMatrix;
        this.modelMatrix = modelMatrix;
    }

    @Override
    public void apply(Program program, Cube cube) {
        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        //TODO: accumulatedRotation = currentRotation * accumulatedRotation
        currentRotation.multiplyWithMatrixAndStore(accumulatedRotation, temporaryMatrix, accumulatedRotation);
        // Rotate the cube taking the overall rotation into account.
        modelMatrix.multiplyWithMatrixAndStore(accumulatedRotation, temporaryMatrix);
    }
}
