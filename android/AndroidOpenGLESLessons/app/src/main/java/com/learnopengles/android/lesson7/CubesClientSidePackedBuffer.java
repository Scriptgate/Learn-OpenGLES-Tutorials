package com.learnopengles.android.lesson7;

import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.program.AttributeVariable.*;

class CubesClientSidePackedBuffer extends Cubes {
    private FloatBuffer cubeBuffer;

    CubesClientSidePackedBuffer(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        cubeBuffer = getInterleavedBuffer(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);
    }

    @Override
    public void render(Program program, int actualCubeFactor) {
        int positionHandle = program.getHandle(POSITION);
        int normalHandle = program.getHandle(NORMAL);
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);

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
