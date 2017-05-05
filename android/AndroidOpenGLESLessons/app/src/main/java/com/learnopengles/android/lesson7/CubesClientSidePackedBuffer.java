package com.learnopengles.android.lesson7;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;

public class CubesClientSidePackedBuffer extends Cubes {
    private FloatBuffer cubeBuffer;

    CubesClientSidePackedBuffer(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        cubeBuffer = getInterleavedBuffer(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);
    }

    @Override
    public void render(int programHandle, int actualCubeFactor) {
        int positionHandle = glGetAttribLocation(programHandle, "a_Position");
        int normalHandle = glGetAttribLocation(programHandle, "a_Normal");
        int textureCoordinateHandle = glGetAttribLocation(programHandle, "a_TexCoordinate");

        final int stride = (POSITION_DATA_SIZE + NORMAL_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE) * BYTES_PER_FLOAT;

        // Pass in the position information
        cubeBuffer.position(0);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, stride, cubeBuffer);

        // Pass in the normal information
        cubeBuffer.position(POSITION_DATA_SIZE);
        glEnableVertexAttribArray(normalHandle);
        glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, stride, cubeBuffer);

        // Pass in the texture information
        cubeBuffer.position(POSITION_DATA_SIZE + NORMAL_DATA_SIZE);
        glEnableVertexAttribArray(textureCoordinateHandle);
        glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, stride, cubeBuffer);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
    }

    @Override
    public void release() {
        cubeBuffer.limit(0);
        cubeBuffer = null;
    }
}
