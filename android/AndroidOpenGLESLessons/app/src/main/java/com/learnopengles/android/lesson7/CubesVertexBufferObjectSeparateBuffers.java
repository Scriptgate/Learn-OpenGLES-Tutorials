package com.learnopengles.android.lesson7;

import net.scriptgate.opengles.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static net.scriptgate.nio.BufferHelper.BYTES_PER_FLOAT;
import static net.scriptgate.opengles.program.AttributeVariable.*;

class CubesVertexBufferObjectSeparateBuffers extends Cubes {

    private final int positionsBufferIndex;
    private final int normalsBufferIndex;
    private final int TextureCoordinatesBufferIndex;

    CubesVertexBufferObjectSeparateBuffers(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        FloatBuffer[] floatBuffers = getBuffers(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);

        FloatBuffer cubePositionsBuffer = floatBuffers[0];
        FloatBuffer cubeNormalsBuffer = floatBuffers[1];
        FloatBuffer cubeTextureCoordinatesBuffer = floatBuffers[2];

        // Second, copy these buffers into OpenGL's memory. After, we don't need to keep the client-side buffers around.
        final int buffers[] = new int[3];
        glGenBuffers(3, buffers, 0);

        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        glBufferData(GL_ARRAY_BUFFER, cubePositionsBuffer.capacity() * BYTES_PER_FLOAT, cubePositionsBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);
        glBufferData(GL_ARRAY_BUFFER, cubeNormalsBuffer.capacity() * BYTES_PER_FLOAT, cubeNormalsBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, buffers[2]);
        glBufferData(GL_ARRAY_BUFFER, cubeTextureCoordinatesBuffer.capacity() * BYTES_PER_FLOAT, cubeTextureCoordinatesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        positionsBufferIndex = buffers[0];
        normalsBufferIndex = buffers[1];
        TextureCoordinatesBufferIndex = buffers[2];

        cubePositionsBuffer.limit(0);
        cubePositionsBuffer = null;
        cubeNormalsBuffer.limit(0);
        cubeNormalsBuffer = null;
        cubeTextureCoordinatesBuffer.limit(0);
        cubeTextureCoordinatesBuffer = null;
    }

    @Override
    public void render(Program program, int actualCubeFactor) {
        program.bind(positionsBufferIndex).to(POSITION);
        program.bind(normalsBufferIndex).to(NORMAL);
        program.bind(TextureCoordinatesBufferIndex).to(TEXTURE_COORDINATE);

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
    }

    @Override
    public void release() {
        // Delete buffers from OpenGL's memory
        final int[] buffersToDelete = new int[] {positionsBufferIndex, normalsBufferIndex, TextureCoordinatesBufferIndex};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}
