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

        final int stride = (POSITION.getSize() + NORMAL.getSize() + TEXTURE_COORDINATE.getSize()) * BYTES_PER_FLOAT;

        // Pass in the position information
        cubeBuffer.position(0);
        int positionHandle = program.getHandle(POSITION);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, POSITION.getSize(), GL_FLOAT, false, stride, cubeBuffer);

        // Pass in the normal information
        cubeBuffer.position(POSITION.getSize());
        int normalHandle = program.getHandle(NORMAL);
        glEnableVertexAttribArray(normalHandle);
        glVertexAttribPointer(normalHandle, NORMAL.getSize(), GL_FLOAT, false, stride, cubeBuffer);

        // Pass in the texture information
        cubeBuffer.position(POSITION.getSize() + NORMAL.getSize());
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);
        glEnableVertexAttribArray(textureCoordinateHandle);
        glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE.getSize(), GL_FLOAT, false, stride, cubeBuffer);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
    }

    @Override
    public void release() {
        cubeBuffer.limit(0);
        cubeBuffer = null;
    }
}
