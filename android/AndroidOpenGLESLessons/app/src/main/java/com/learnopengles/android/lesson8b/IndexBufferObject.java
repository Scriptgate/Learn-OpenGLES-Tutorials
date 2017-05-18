package com.learnopengles.android.lesson8b;

import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_SHORT;
import static com.learnopengles.android.common.BufferHelper.allocateFloatBuffer;
import static com.learnopengles.android.common.BufferHelper.allocateShortBuffer;
import static com.learnopengles.android.lesson8b.VertexDataBufferFactory.POSITION_DATA_SIZE_IN_ELEMENTS;
import static com.learnopengles.android.lesson8b.VertexDataBufferFactory.TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;

public class IndexBufferObject {

    private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT;

    public final int vboBufferIndex;
    public final int iboBufferIndex;

    public int indexCount = 0;

    IndexBufferObject(int vboBufferIndex, int iboBufferIndex) {
        this.vboBufferIndex = vboBufferIndex;
        this.iboBufferIndex = iboBufferIndex;
    }

    static IndexBufferObject allocate(int numberOfCubes) {

        final int[] indices = new int[2];
        glGenBuffers(indices.length, indices, 0);

        final int vboBufferIndex = indices[0];
        final int iboBufferIndex = indices[1];

        final FloatBuffer heightMapVertexDataBuffer = allocateFloatBuffer(7 * numberOfCubes * (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS));
        final ShortBuffer heightMapIndexDataBuffer = allocateShortBuffer(18 * numberOfCubes);

        glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);
        glBufferData(GL_ARRAY_BUFFER, heightMapVertexDataBuffer.capacity() * BYTES_PER_FLOAT, heightMapVertexDataBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, heightMapIndexDataBuffer.capacity() * BYTES_PER_SHORT, heightMapIndexDataBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        return new IndexBufferObject(vboBufferIndex, iboBufferIndex);
    }

    void addData(FloatBuffer vertexBuffer, ShortBuffer indexBuffer) {
        long start = System.currentTimeMillis();

        int vertexDataOffsetInBytes = 0;
        int vertexDataSizeInBytes = vertexBuffer.capacity() * BYTES_PER_FLOAT;
        System.out.println("Adding data (" + vertexDataSizeInBytes + " bytes) with " + vertexDataOffsetInBytes + " bytes offset to the vertex buffer");

        glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);
        glBufferSubData(GL_ARRAY_BUFFER, vertexDataOffsetInBytes, vertexDataSizeInBytes, vertexBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        int indexDataOffsetInBytes = 0;
        int indexDataSizeInBytes = indexBuffer.capacity() * BYTES_PER_SHORT;
        System.out.println("Adding data (" + indexDataSizeInBytes + " bytes) with " + indexDataOffsetInBytes + " bytes offset to the index buffer");

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, indexDataOffsetInBytes, indexDataSizeInBytes, indexBuffer);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        indexCount += indexBuffer.capacity();

        vertexBuffer.limit(0);
        indexBuffer.limit(0);

        long elapsedTimeMillis = (System.currentTimeMillis() - start);
        int totalDataInBytes = vertexBuffer.capacity() * BYTES_PER_FLOAT + indexBuffer.capacity() * BYTES_PER_SHORT;
        System.out.println("IBO transfer from CPU to GPU for " + indexBuffer.capacity() + " events (" + totalDataInBytes + " bytes) took " + elapsedTimeMillis + " ms.");
    }

    public void render(Program program) {
        glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);

        int positionAttribute = program.getHandle(POSITION);
        glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GL_FLOAT, false, STRIDE, 0);
        glEnableVertexAttribArray(positionAttribute);

        int textureCoordinateAttribute = program.getHandle(TEXTURE_COORDINATE);
        glVertexAttribPointer(textureCoordinateAttribute, TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS, GL_FLOAT, false, STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);
        glEnableVertexAttribArray(textureCoordinateAttribute);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_SHORT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}