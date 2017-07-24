package com.learnopengles.android.lesson7;

import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.program.AttributeVariable.*;

class CubesVertexBufferObjectPackedBuffers extends Cubes {

    private final int cubeBufferIdx;

    CubesVertexBufferObjectPackedBuffers(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        FloatBuffer cubeBuffer = getInterleavedBuffer(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);

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

    @Override
    public void render(Program program, int actualCubeFactor) {
        int positionHandle = program.getHandle(POSITION);
        int normalHandle = program.getHandle(NORMAL);
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);

        final int stride = (POSITION_DATA_SIZE + NORMAL_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE) * BYTES_PER_FLOAT;

        // Pass in the position information
        glBindBuffer(GL_ARRAY_BUFFER, cubeBufferIdx);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, stride, 0);

        // Pass in the normal information
        glBindBuffer(GL_ARRAY_BUFFER, cubeBufferIdx);
        glEnableVertexAttribArray(normalHandle);
        glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, stride, POSITION_DATA_SIZE * BYTES_PER_FLOAT);

        // Pass in the texture information
        glBindBuffer(GL_ARRAY_BUFFER, cubeBufferIdx);
        glEnableVertexAttribArray(textureCoordinateHandle);
        glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, stride, (POSITION_DATA_SIZE + NORMAL_DATA_SIZE) * BYTES_PER_FLOAT);

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
    }

    @Override
    public void release() {
        // Delete buffers from OpenGL's memory
        final int[] buffersToDelete = new int[] {cubeBufferIdx};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}
