package com.learnopengles.android.lesson7;

import net.scriptgate.opengles.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static net.scriptgate.opengles.program.AttributeVariable.*;

class CubesClientSideSeparateBuffers extends Cubes {
    private FloatBuffer cubePositions;
    private FloatBuffer cubeNormals;
    private FloatBuffer cubeTextureCoordinates;

    CubesClientSideSeparateBuffers(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        FloatBuffer[] buffers = getBuffers(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);

        this.cubePositions = buffers[0];
        this.cubeNormals = buffers[1];
        this.cubeTextureCoordinates = buffers[2];
    }

    @Override
    public void render(Program program, int actualCubeFactor) {
        program.pass(cubePositions).to(POSITION);
        program.pass(cubeNormals).to(NORMAL);
        program.pass(cubeTextureCoordinates).to(TEXTURE_COORDINATE);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
    }

    @Override
    public void release() {
        cubePositions.limit(0);
        cubePositions = null;
        cubeNormals.limit(0);
        cubeNormals = null;
        cubeTextureCoordinates.limit(0);
        cubeTextureCoordinates = null;
    }
}