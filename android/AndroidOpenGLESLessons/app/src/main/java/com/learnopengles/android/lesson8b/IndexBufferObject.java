package com.learnopengles.android.lesson8b;

import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_SHORT;
import static com.learnopengles.android.common.BufferHelper.allocateFloatBuffer;
import static com.learnopengles.android.common.BufferHelper.allocateShortBuffer;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;

public class IndexBufferObject {

    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS = 2;

    private static final int INDICES_PER_CUBE = 18;
    private static final int VERTICES_PER_CUBE = 7;

    final int vboBufferIndex;
    final int iboBufferIndex;

    private int indexCount = 0;

    private int count = 0;
    private final int capacity;

    private IndexBufferObject(int vboBufferIndex, int iboBufferIndex, int capacity) {
        this.vboBufferIndex = vboBufferIndex;
        this.iboBufferIndex = iboBufferIndex;
        this.capacity = capacity;

        glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);
        int vertexDataSizeInBytes = getVertexBufferSize(capacity) * BYTES_PER_FLOAT;
        glBufferData(GL_ARRAY_BUFFER, vertexDataSizeInBytes, null, GL_DYNAMIC_DRAW);
        logData("Buffer Data", "vertex buffer", vertexDataSizeInBytes, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
        int indexDataSizeInBytes = getIndexBufferSize(capacity) * BYTES_PER_SHORT;
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexDataSizeInBytes, null, GL_DYNAMIC_DRAW);
        logData("Buffer Data", "index buffer", indexDataSizeInBytes, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    static IndexBufferObject allocate(int numberOfCubes) {
        final int[] indices = new int[2];
        glGenBuffers(indices.length, indices, 0);

        final int vboBufferIndex = indices[0];
        final int iboBufferIndex = indices[1];

        return new IndexBufferObject(vboBufferIndex, iboBufferIndex, numberOfCubes);
    }

    void addData(IndexBufferObjectCreator creator) {
        long start = System.currentTimeMillis();

        IndexBufferObjectData data = creator.createData((short) (count  * VERTICES_PER_CUBE));

        glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);
        {
            int positionDataSizeInBytes = data.positionDataBuffer.capacity() * BYTES_PER_FLOAT;
            int positionDataOffsetInBytes = count * VERTICES_PER_CUBE * POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT;
            logData("Buffer Sub Data", "position data buffer", positionDataSizeInBytes, positionDataOffsetInBytes);
            glBufferSubData(GL_ARRAY_BUFFER, positionDataOffsetInBytes, positionDataSizeInBytes, data.positionDataBuffer);

            int textureDataSizeInBytes = data.textureDataBuffer.capacity() * BYTES_PER_FLOAT;
            int totalPositionDataSizeInBytes = (capacity * VERTICES_PER_CUBE * POSITION_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT;
            int textureDataOffsetInBytes = totalPositionDataSizeInBytes + count * VERTICES_PER_CUBE * TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT;
            logData("Buffer Sub Data", "texture data buffer", textureDataSizeInBytes, textureDataOffsetInBytes);
            glBufferSubData(GL_ARRAY_BUFFER, textureDataOffsetInBytes, textureDataSizeInBytes, data.textureDataBuffer);
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
        {
            int indexDataOffsetInBytes = indexCount * BYTES_PER_SHORT;
            int indexDataSizeInBytes = data.indexBuffer.capacity() * BYTES_PER_SHORT;
            logData("Buffer Sub Data", "index buffer", indexDataSizeInBytes, indexDataOffsetInBytes);

            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, indexDataOffsetInBytes, indexDataSizeInBytes, data.indexBuffer);
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        indexCount += data.indexBuffer.capacity();
        count += data.numberOfCubes;

        data.release();

        long elapsedTimeMillis = (System.currentTimeMillis() - start);
        int totalDataInBytes = (data.positionDataBuffer.capacity() + data.textureDataBuffer.capacity()) * BYTES_PER_FLOAT + data.indexBuffer.capacity() * BYTES_PER_SHORT;
        System.out.println("IBO transfer from CPU to GPU for " + data.indexBuffer.capacity() + " events (" + totalDataInBytes + " bytes) took " + elapsedTimeMillis + " ms.");
        System.out.println("IBO status: ");
        System.out.println("\t- capacity: " + count + "/" + capacity);
        System.out.println("\t- index count: " + indexCount);
    }

    private static void logData(String method, String buffer, int sizeInBytes, int offsetInBytes) {
        System.out.println(method + ": " + buffer);
        System.out.println("\t- bytes: " + sizeInBytes);
        System.out.println("\t- offset: " + offsetInBytes);
    }

    public void render(Program program) {
        glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);

        int positionAttribute = program.getHandle(POSITION);
        glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(positionAttribute);

        int textureCoordinateAttribute = program.getHandle(TEXTURE_COORDINATE);
        glVertexAttribPointer(textureCoordinateAttribute, TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS, GL_FLOAT, false, 0, (capacity * VERTICES_PER_CUBE) * POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);
        glEnableVertexAttribArray(textureCoordinateAttribute);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_SHORT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    static FloatBuffer allocatePositionDataBuffer(int numberOfCubes) {
        return allocateFloatBuffer(VERTICES_PER_CUBE * numberOfCubes * POSITION_DATA_SIZE_IN_ELEMENTS);
    }

    static FloatBuffer allocateTextureDataBuffer(int numberOfCubes) {
        return allocateFloatBuffer(VERTICES_PER_CUBE * numberOfCubes * TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS);
    }

    private static int getVertexBufferSize(int numberOfCubes) {
        return VERTICES_PER_CUBE * numberOfCubes * (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS);
    }

    static ShortBuffer allocateIndexBuffer(int numberOfCubes) {
        return allocateShortBuffer(INDICES_PER_CUBE * numberOfCubes);
    }

    private static int getIndexBufferSize(int numberOfCubes) {
        return INDICES_PER_CUBE * numberOfCubes;
    }
}