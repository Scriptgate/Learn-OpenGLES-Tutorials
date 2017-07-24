package com.learnopengles.android.lesson6;

import net.scriptgate.opengles.light.Light;
import net.scriptgate.opengles.matrix.ModelMatrix;
import net.scriptgate.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.opengles.matrix.ProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import net.scriptgate.opengles.cube.Cube;
import net.scriptgate.opengles.program.Program;

import static android.opengl.GLES20.*;
import static net.scriptgate.opengles.program.UniformVariable.*;
import static net.scriptgate.opengles.program.AttributeVariable.*;

class CubeRenderer {

    private final Program program;
    private final ModelMatrix modelMatrix;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final ModelViewProjectionMatrix mvpMatrix;
    private final float[] accumulatedRotation;
    private final ModelMatrix currentRotation;
    private final float[] temporaryMatrix;
    private final Light light;

    CubeRenderer(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix, float[] accumulatedRotation, ModelMatrix currentRotation, float[] temporaryMatrix, Light light) {
        this.program = program;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mvpMatrix = mvpMatrix;
        this.accumulatedRotation = accumulatedRotation;
        this.currentRotation = currentRotation;
        this.temporaryMatrix = temporaryMatrix;
        this.light = light;
    }

    void useForRendering() {
        program.useForRendering();
    }

    void draw(Cube cube) {

        cube.apply(modelMatrix);

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        //TODO: accumulatedRotation = currentRotation * accumulatedRotation
        currentRotation.multiplyWithMatrixAndStore(accumulatedRotation, temporaryMatrix, accumulatedRotation);
        // Rotate the cube taking the overall rotation into account.
        modelMatrix.multiplyWithMatrixAndStore(accumulatedRotation, temporaryMatrix);

        program.pass(cube.getData(POSITION)).to(POSITION);
        program.pass(cube.getData(NORMAL)).to(NORMAL);
        program.pass(cube.getData(TEXTURE_COORDINATE)).to(TEXTURE_COORDINATE);

        program.bindTexture(cube.getTexture());

        mvpMatrix.multiply(modelMatrix, viewMatrix);
        mvpMatrix.passTo(program.getHandle(MV_MATRIX));
        mvpMatrix.multiply(projectionMatrix, temporaryMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glUniform3fv(program.getHandle(LIGHT_POSITION), 1, light.getPositionInEyeSpace(), 0);

        glDrawArrays(GL_TRIANGLES, 0, 36);

    }
}
