package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.*;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;
import static java.util.Arrays.asList;

public class IndexBufferObjects {

    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "IndexBufferObjects";

    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS = 2;

    private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT;

    private BufferPair bufferA;
    private BufferPair bufferB;

    private int textureHandle;

    private static final int CUBES_PER_BUFFER = 8;

    public IndexBufferObjects(int textureHandle) {
        this.textureHandle = textureHandle;


        final int[] bufferIndices = new int[4];
        glGenBuffers(bufferIndices.length, bufferIndices, 0);

        bufferA = BufferPair.allocate(bufferIndices[0], bufferIndices[1]);
        bufferB = BufferPair.allocate(bufferIndices[2], bufferIndices[3]);

        int indexOffset = 0;
        bufferA.addData(
                buildVertexData(CUBES_PER_BUFFER, new Point3D(), indexOffset),
                buildIndexData(CUBES_PER_BUFFER)
        );
        indexOffset += CUBES_PER_BUFFER;
        bufferB.addData(
                buildVertexData(CUBES_PER_BUFFER, new Point3D(0, 0.2f * indexOffset, 0), indexOffset),
                buildIndexData(CUBES_PER_BUFFER)
        );


    }

    private static ShortBuffer buildIndexData(int numberOfCubes) {

        ShortBuffer indexBuffer = allocateShortBuffer(18 * numberOfCubes);

        short indexOffset = 0;

        for (int i = 0; i < numberOfCubes; i++) {
            final short frontA = indexOffset++;
            final short frontB = indexOffset++;
            final short frontC = indexOffset++;
            final short frontD = indexOffset++;
            final short backA = indexOffset++;
            final short backB = indexOffset++;
            final short backD = indexOffset++;

            short[] frontFace = new short[]{frontA, frontB, frontC, frontD};
            short[] rightFace = new short[]{frontD, frontB, backD, backB};
            short[] topFace = new short[]{backB, frontB, backA, frontA};

            for (short[] face : asList(frontFace, rightFace, topFace)) {
                indexBuffer.put(face[0]);
                indexBuffer.put(face[2]);
                indexBuffer.put(face[1]);
                indexBuffer.put(face[2]);
                indexBuffer.put(face[3]);
                indexBuffer.put(face[1]);
            }
        }
        indexBuffer.position(0);
        return indexBuffer;
    }

    private static FloatBuffer buildVertexData(int numberOfCubes, Point3D offset, int indexOffset) {
        float width = 1;
        float height = 0.2f;
        float depth = 1;

        FloatBuffer vertexBuffer = allocateFloatBuffer(7 * numberOfCubes * (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS));

        Point3D position = new Point3D(offset.x, offset.y, offset.z);

        for (int i = 0; i < numberOfCubes; i++) {
            TextureTriangle texture = new TextureTriangle(i + indexOffset);

            //@formatter:off
            final Vertex frontA = new Vertex(new Point3D(position.x,         position.y + height, position.z + depth), texture.p1);
            final Vertex frontB = new Vertex(new Point3D(position.x + width, position.y + height, position.z + depth), texture.p2);
            final Vertex frontC = new Vertex(new Point3D(position.x,         position.y,          position.z + depth), texture.p3);
            final Vertex frontD = new Vertex(new Point3D(position.x + width, position.y,          position.z + depth), texture.p1);
            final Vertex backA  = new Vertex(new Point3D(position.x,         position.y + height, position.z), texture.p3);
            final Vertex backB  = new Vertex(new Point3D(position.x + width, position.y + height, position.z), texture.p1);
            final Vertex backD  = new Vertex(new Point3D(position.x + width, position.y,          position.z), texture.p3);
            //@formatter:on

            for (Vertex point : asList(frontA, frontB, frontC, frontD, backA, backB, backD)) {
                point.putIn(vertexBuffer);
            }

            position.y += 0.2f;
        }
        vertexBuffer.position(0);
        return vertexBuffer;
    }

    private static class BufferPair {
        private final int vboBufferIndex;
        private final int iboBufferIndex;

        private int indexCount = 0;

        BufferPair(int vboBufferIndex, int iboBufferIndex) {
            this.vboBufferIndex = vboBufferIndex;
            this.iboBufferIndex = iboBufferIndex;
        }

        static BufferPair allocate(int vboBufferIndex, int iboBufferIndex) {
            final FloatBuffer heightMapVertexDataBuffer = allocateFloatBuffer(7 * CUBES_PER_BUFFER * (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS));
            final ShortBuffer heightMapIndexDataBuffer = allocateShortBuffer(18 * CUBES_PER_BUFFER);

            glBindBuffer(GL_ARRAY_BUFFER, vboBufferIndex);
            glBufferData(GL_ARRAY_BUFFER, heightMapVertexDataBuffer.capacity() * BYTES_PER_FLOAT, heightMapVertexDataBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBufferIndex);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, heightMapIndexDataBuffer.capacity() * BYTES_PER_SHORT, heightMapIndexDataBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

            return new BufferPair(vboBufferIndex, iboBufferIndex);
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
    }

    void render(Program program) {
        setTexture(program);

        for (BufferPair buffer : asList(bufferA, bufferB)) {
            glBindBuffer(GL_ARRAY_BUFFER, buffer.vboBufferIndex);

            int positionAttribute = program.getHandle(POSITION);
            glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GL_FLOAT, false, STRIDE, 0);
            glEnableVertexAttribArray(positionAttribute);

            int textureCoordinateAttribute = program.getHandle(TEXTURE_COORDINATE);
            glVertexAttribPointer(textureCoordinateAttribute, TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS, GL_FLOAT, false, STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);
            glEnableVertexAttribArray(textureCoordinateAttribute);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer.iboBufferIndex);
            glDrawElements(GL_TRIANGLES, buffer.indexCount, GL_UNSIGNED_SHORT, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    private void setTexture(Program program) {
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(program.getHandle(TEXTURE), 0);
    }

    void release() {
        final int[] buffersToDelete = new int[]{bufferA.vboBufferIndex, bufferA.iboBufferIndex, bufferB.vboBufferIndex, bufferB.iboBufferIndex};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}