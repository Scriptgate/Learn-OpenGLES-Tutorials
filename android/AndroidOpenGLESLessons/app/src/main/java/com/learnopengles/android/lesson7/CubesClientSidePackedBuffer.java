package com.learnopengles.android.lesson7;

import net.scriptgate.opengles.program.AttributeVariable;
import net.scriptgate.opengles.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static net.scriptgate.opengles.program.AttributeVariable.*;

class CubesClientSidePackedBuffer extends Cubes {
    private FloatBuffer cubeBuffer;

    CubesClientSidePackedBuffer(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        cubeBuffer = getInterleavedBuffer(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);
    }

    @Override
    public void render(Program program, int actualCubeFactor) {

        AttributeVariable[] structure = {POSITION, NORMAL, TEXTURE_COORDINATE};

        program.pass(cubeBuffer, structure).at(0).to(POSITION);
        program.pass(cubeBuffer, structure).after(POSITION).to(NORMAL);
        program.pass(cubeBuffer, structure).after(POSITION, NORMAL).to(TEXTURE_COORDINATE);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
    }

    @Override
    public void release() {
        cubeBuffer.limit(0);
        cubeBuffer = null;
    }
}
