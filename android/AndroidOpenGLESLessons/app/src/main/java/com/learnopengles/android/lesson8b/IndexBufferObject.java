package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.*;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;
import static java.util.Arrays.asList;

public class IndexBufferObject {

    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "IndexBufferObject";

    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS = 2;

    private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT;

    private final int vboBufferIndex;
    private final int iboBufferIndex;

    private int indexCount;
    private int textureHandle;

    public IndexBufferObject(int textureHandle) {
        this.textureHandle = textureHandle;

        final FloatBuffer heightMapVertexDataBuffer = buildVertexData(new Point3D(), 1, 0.2f, 1);
        final ShortBuffer heightMapIndexDataBuffer = buildIndexData();

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

    private ShortBuffer buildIndexData() {
        final short frontA = 0;
        final short frontB = 1;
        final short frontC = 2;
        final short frontD = 3;
        final short backA = 4;
        final short backB = 5;
        final short backD = 6;

        short[] frontFace = new short[]{frontA, frontB, frontC, frontD};
        short[] rightFace = new short[]{frontD, frontB, backD, backB};
        short[] topFace = new short[]{backB, frontB, backA, frontA};

        short[] data = new short[18];

        int offset = 0;
        for (short[] face : asList(frontFace, rightFace, topFace)) {
            data[offset++] = face[0];
            data[offset++] = face[2];
            data[offset++] = face[1];
            data[offset++] = face[2];
            data[offset++] = face[3];
            data[offset++] = face[1];
        }
        return allocateBuffer(data);
    }

    private FloatBuffer buildVertexData(Point3D position, float width, float height, float depth) {
        TextureTriangle texture = new TextureTriangle(15);

        //@formatter:off
        final Vertex frontA = new Vertex(new Point3D(position.x,         position.y + height, position.z + depth), texture.p1);
        final Vertex frontB = new Vertex(new Point3D(position.x + width, position.y + height, position.z + depth), texture.p2);
        final Vertex frontC = new Vertex(new Point3D(position.x,         position.y,          position.z + depth), texture.p3);
        final Vertex frontD = new Vertex(new Point3D(position.x + width, position.y,          position.z + depth), texture.p1);
        final Vertex backA  = new Vertex(new Point3D(position.x,         position.y + height, position.z), texture.p3);
        final Vertex backB  = new Vertex(new Point3D(position.x + width, position.y + height, position.z), texture.p1);
        final Vertex backD  = new Vertex(new Point3D(position.x + width, position.y,          position.z), texture.p3);
        //@formatter:on

        List<Vertex> points = asList(frontA, frontB, frontC, frontD, backA, backB, backD);

        FloatBuffer vertexBuffer = allocateFloatBuffer(points.size() * (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS));
        for (Vertex point : points) {
            point.putIn(vertexBuffer);
        }
        vertexBuffer.position(0);
        return vertexBuffer;
    }

    void render(Program program) {
        setTexture(program);

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

    private void setTexture(Program program) {
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(program.getHandle(TEXTURE), 0);
    }

    void release() {
        final int[] buffersToDelete = new int[]{vboBufferIndex, iboBufferIndex};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}