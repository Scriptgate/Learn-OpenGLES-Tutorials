package com.learnopengles.android.lesson7;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;

class CubesVertexBufferObjectSeparateBuffers extends Cubes {

    private final int cubePositionsBufferIdx;
    private final int cubeNormalsBufferIdx;
    private final int cubeTexCoordsBufferIdx;

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

        cubePositionsBufferIdx = buffers[0];
        cubeNormalsBufferIdx = buffers[1];
        cubeTexCoordsBufferIdx = buffers[2];

        cubePositionsBuffer.limit(0);
        cubePositionsBuffer = null;
        cubeNormalsBuffer.limit(0);
        cubeNormalsBuffer = null;
        cubeTextureCoordinatesBuffer.limit(0);
        cubeTextureCoordinatesBuffer = null;
    }

    @Override
    public void render(int programHandle, int actualCubeFactor) {
        int positionHandle = glGetAttribLocation(programHandle, "a_Position");
        int normalHandle = glGetAttribLocation(programHandle, "a_Normal");
        int textureCoordinateHandle = glGetAttribLocation(programHandle, "a_TexCoordinate");

        // Pass in the position information
        glBindBuffer(GL_ARRAY_BUFFER, cubePositionsBufferIdx);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, 0);

        // Pass in the normal information
        glBindBuffer(GL_ARRAY_BUFFER, cubeNormalsBufferIdx);
        glEnableVertexAttribArray(normalHandle);
        glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, 0);

        // Pass in the texture information
        glBindBuffer(GL_ARRAY_BUFFER, cubeTexCoordsBufferIdx);
        glEnableVertexAttribArray(textureCoordinateHandle);
        glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, 0);

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
    }

    @Override
    public void release() {
        // Delete buffers from OpenGL's memory
        final int[] buffersToDelete = new int[] {cubePositionsBufferIdx, cubeNormalsBufferIdx, cubeTexCoordsBufferIdx};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}
