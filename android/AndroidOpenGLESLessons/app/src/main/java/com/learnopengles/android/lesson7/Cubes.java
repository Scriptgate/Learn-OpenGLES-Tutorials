package com.learnopengles.android.lesson7;

import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.common.BufferHelper.allocateBuffer;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

abstract class Cubes {

    abstract void render(Program program, int actualCubeFactor);

    abstract void release();

    FloatBuffer[] getBuffers(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        // First, copy cube information into client-side floating point buffers.
        final FloatBuffer cubePositionsBuffer;
        final FloatBuffer cubeNormalsBuffer;
        final FloatBuffer cubeTextureCoordinatesBuffer;

        {
            cubePositionsBuffer = allocateBuffer(cubePositions);
            cubePositionsBuffer.position(0);
        }

        int generatedCubeFactorToPowerThree = generatedCubeFactor * generatedCubeFactor * generatedCubeFactor;
        {
            cubeNormalsBuffer = allocateDirect(cubeNormals.length * BYTES_PER_FLOAT * generatedCubeFactorToPowerThree).order(nativeOrder()).asFloatBuffer();
            for (int i = 0; i < generatedCubeFactorToPowerThree; i++) {
                cubeNormalsBuffer.put(cubeNormals);
            }
            cubeNormalsBuffer.position(0);
        }

        {
            cubeTextureCoordinatesBuffer = allocateDirect(cubeTextureCoordinates.length * BYTES_PER_FLOAT * generatedCubeFactorToPowerThree).order(nativeOrder()).asFloatBuffer();
            for (int i = 0; i < generatedCubeFactorToPowerThree; i++) {
                cubeTextureCoordinatesBuffer.put(cubeTextureCoordinates);
            }
            cubeTextureCoordinatesBuffer.position(0);
        }

        return new FloatBuffer[]{cubePositionsBuffer, cubeNormalsBuffer, cubeTextureCoordinatesBuffer};
    }

    FloatBuffer getInterleavedBuffer(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        final int cubeDataLength = cubePositions.length
                + (cubeNormals.length * generatedCubeFactor * generatedCubeFactor * generatedCubeFactor)
                + (cubeTextureCoordinates.length * generatedCubeFactor * generatedCubeFactor * generatedCubeFactor);
        int cubePositionOffset = 0;
        int cubeNormalOffset = 0;
        int cubeTextureOffset = 0;

        final FloatBuffer cubeBuffer = allocateDirect(cubeDataLength * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();

        for (int i = 0; i < generatedCubeFactor * generatedCubeFactor * generatedCubeFactor; i++) {
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
