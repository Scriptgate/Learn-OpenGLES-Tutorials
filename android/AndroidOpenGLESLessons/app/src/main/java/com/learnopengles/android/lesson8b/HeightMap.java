package com.learnopengles.android.lesson8b;

import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.allocateBuffer;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.POSITION;

public class HeightMap {

    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "HeightMap";

    /**
     * Additional constants.
     */
    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int COLOR_DATA_SIZE_IN_ELEMENTS = 4;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;

    private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT;

    private static final int SIZE_PER_SIDE = 32;
    private static final float MIN_POSITION = -5f;
    private static final float POSITION_RANGE = 10f;

    private final int vboBufferIndex;
    private final int iboBufferIndex;

    private int indexCount;

    public HeightMap() {

        final int floatsPerVertex = POSITION_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS;

        final FloatBuffer heightMapVertexDataBuffer = allocateBuffer(buildVertexData(floatsPerVertex, SIZE_PER_SIDE, SIZE_PER_SIDE));
        final ShortBuffer heightMapIndexDataBuffer = allocateBuffer(buildIndexData(SIZE_PER_SIDE, SIZE_PER_SIDE));

        indexCount = heightMapIndexDataBuffer.capacity();

        final int[] buffers = new int[2];
        glGenBuffers(2, buffers, 0);
        vboBufferIndex = buffers[0];
        iboBufferIndex = buffers[1];

        glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);
        glBufferData(GL_ARRAY_BUFFER, heightMapVertexDataBuffer.capacity() * BYTES_PER_FLOAT, heightMapVertexDataBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, heightMapIndexDataBuffer.capacity() * BYTES_PER_SHORT, heightMapIndexDataBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private short[] buildIndexData(int xLength, int yLength) {
        // Now build the index data
        final int numStripsRequired = yLength - 1;
        final int numDegensRequired = 2 * (numStripsRequired - 1);
        final int verticesPerStrip = 2 * xLength;

        final short[] heightMapIndexData = new short[(verticesPerStrip * numStripsRequired) + numDegensRequired];

        int offset = 0;

        for (int y = 0; y < yLength - 1; y++) {
            if (y > 0) {
                // Degenerate begin: repeat first vertex
                heightMapIndexData[offset++] = (short) (y * yLength);
            }

            for (int x = 0; x < xLength; x++) {
                // One part of the strip
                heightMapIndexData[offset++] = (short) ((y * yLength) + x);
                heightMapIndexData[offset++] = (short) (((y + 1) * yLength) + x);
            }

            if (y < yLength - 2) {
                // Degenerate end: repeat last vertex
                heightMapIndexData[offset++] = (short) (((y + 1) * yLength) + (xLength - 1));
            }
        }
        return heightMapIndexData;
    }

    private float[] buildVertexData(int floatsPerVertex, int xLength, int yLength) {
        final float[] heightMapVertexData = new float[xLength * yLength * floatsPerVertex];

        int offset = 0;

        // First, build the data for the vertex buffer
        for (int y = 0; y < yLength; y++) {
            for (int x = 0; x < xLength; x++) {
                final float xRatio = x / (float) (xLength - 1);

                // Build our heightmap from the top down, so that our triangles are counter-clockwise.
                final float yRatio = 1f - (y / (float) (yLength - 1));

                final float xPosition = MIN_POSITION + (xRatio * POSITION_RANGE);
                final float yPosition = MIN_POSITION + (yRatio * POSITION_RANGE);

                // Position
                heightMapVertexData[offset++] = xPosition;
                heightMapVertexData[offset++] = yPosition;
                heightMapVertexData[offset++] = ((xPosition * xPosition) + (yPosition * yPosition)) / 10f;

                // Add some fancy colors.
                heightMapVertexData[offset++] = xRatio;
                heightMapVertexData[offset++] = yRatio;
                heightMapVertexData[offset++] = 0.5f;
                heightMapVertexData[offset++] = 1f;
            }
        }
        return heightMapVertexData;
    }

    void render(Program program) {
        glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);

        int positionAttribute = program.getHandle(POSITION);
        glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GL_FLOAT, false, STRIDE, 0);
        glEnableVertexAttribArray(positionAttribute);

        int colorAttribute = program.getHandle(COLOR);
        glVertexAttribPointer(colorAttribute, COLOR_DATA_SIZE_IN_ELEMENTS, GL_FLOAT, false, STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);
        glEnableVertexAttribArray(colorAttribute);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
        glDrawElements(GL_TRIANGLE_STRIP, indexCount, GL_UNSIGNED_SHORT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    void release() {
        final int[] buffersToDelete = new int[]{vboBufferIndex, iboBufferIndex};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}