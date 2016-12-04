package com.learnopengles.android.lesson7;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;

public class CubesClientSide extends Cubes {
    private FloatBuffer cubePositions;
    private FloatBuffer cubeNormals;
    private FloatBuffer cubeTextureCoordinates;

    CubesClientSide(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
        FloatBuffer[] buffers = getBuffers(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);

        this.cubePositions = buffers[0];
        this.cubeNormals = buffers[1];
        this.cubeTextureCoordinates = buffers[2];
    }

    @Override
    public void render(int programHandle, int actualCubeFactor) {
        int positionHandle = glGetAttribLocation(programHandle, "a_Position");
        int normalHandle = glGetAttribLocation(programHandle, "a_Normal");
        int textureCoordinateHandle = glGetAttribLocation(programHandle, "a_TexCoordinate");

        // Pass in the position information
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, cubePositions);

        // Pass in the normal information
        glEnableVertexAttribArray(normalHandle);
        glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, cubeNormals);

        // Pass in the texture information
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