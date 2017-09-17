package com.learnopengles.android.lesson7b;

import net.scriptgate.android.opengles.program.AttributeVariable;
import net.scriptgate.android.opengles.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static net.scriptgate.android.nio.BufferHelper.BYTES_PER_FLOAT;
import static net.scriptgate.android.nio.BufferHelper.allocateFloatBuffer;
import static net.scriptgate.android.opengles.program.AttributeVariable.*;

class CubesVertexBufferObjectPackedBuffers {

    private final int cubeBufferIdx;

    CubesVertexBufferObjectPackedBuffers(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int numberOfCubes) {
        FloatBuffer cubeBuffer = getInterleavedBuffer(cubePositions, cubeNormals, cubeTextureCoordinates, numberOfCubes);

        // Second, copy these buffers into OpenGL's memory. After, we don't need to keep the client-side buffers around.
        final int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0);

        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        glBufferData(GL_ARRAY_BUFFER, cubeBuffer.capacity() * BYTES_PER_FLOAT, cubeBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        cubeBufferIdx = buffers[0];

        cubeBuffer.limit(0);
        cubeBuffer = null;
    }

    void render(Program program, int numberOfCubes) {

        AttributeVariable[] structure = {POSITION, NORMAL, TEXTURE_COORDINATE};

        program.bind(cubeBufferIdx, structure).at(0).to(POSITION);
        program.bind(cubeBufferIdx, structure).after(POSITION).to(NORMAL);
        program.bind(cubeBufferIdx, structure).after(POSITION, NORMAL).to(TEXTURE_COORDINATE);

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0, numberOfCubes * 36);
    }

    void release() {
        // Delete buffers from OpenGL's memory
        final int[] buffersToDelete = new int[] {cubeBufferIdx};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }

    private FloatBuffer getInterleavedBuffer(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int numberOfCubes) {
        final int cubeDataLength = cubePositions.length
                + (cubeNormals.length * numberOfCubes)
                + (cubeTextureCoordinates.length * numberOfCubes);
        int cubePositionOffset = 0;
        int cubeNormalOffset = 0;
        int cubeTextureOffset = 0;

        final FloatBuffer cubeBuffer = allocateFloatBuffer(cubeDataLength);

        for (int i = 0; i < numberOfCubes; i++) {
            for (int v = 0; v < 36; v++) {
                cubeBuffer.put(cubePositions, cubePositionOffset, POSITION.getSize());
                cubePositionOffset += POSITION.getSize();
                cubeBuffer.put(cubeNormals, cubeNormalOffset, NORMAL.getSize());
                cubeNormalOffset += NORMAL.getSize();
                cubeBuffer.put(cubeTextureCoordinates, cubeTextureOffset, TEXTURE_COORDINATE.getSize());
                cubeTextureOffset += TEXTURE_COORDINATE.getSize();
            }

            // The normal and texture data is repeated for each cube.
            cubeNormalOffset = 0;
            cubeTextureOffset = 0;
        }

        cubeBuffer.position(0);

        return cubeBuffer;
    }
}
