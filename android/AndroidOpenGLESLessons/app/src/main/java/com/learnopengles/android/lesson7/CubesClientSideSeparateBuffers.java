package com.learnopengles.android.lesson7;

import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.program.AttributeVariable.*;

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

        // Pass in the position information
        int positionHandle = program.getHandle(POSITION);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, cubePositions);

        // Pass in the normal information
        int normalHandle = program.getHandle(NORMAL);
        glEnableVertexAttribArray(normalHandle);
        glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, cubeNormals);

        // Pass in the texture information
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);
        glEnableVertexAttribArray(textureCoordinateHandle);
        glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, cubeTextureCoordinates);

        // Draw the cubes.
        glDrawArrays(GL_TRIANGLES, 0,actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
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