package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.ColorPoint3D;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.*;
import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.program.AttributeVariable.*;
import static java.util.Arrays.asList;

public class HeightMap {

    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "HeightMap";

    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int COLOR_DATA_SIZE_IN_ELEMENTS = 4;

    private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT;

    private final int vboBufferIndex;
    private final int iboBufferIndex;

    private int indexCount;

    public HeightMap() {

        final FloatBuffer heightMapVertexDataBuffer = allocateBuffer(buildVertexData(new Point3D(), BLUE, 1, 0.2f, 1));
        final ShortBuffer heightMapIndexDataBuffer = allocateBuffer(buildIndexData());

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

    private short[] buildIndexData() {
        final short frontA = 0;
        final short frontB = 1;
        final short frontC = 2;
        final short frontD = 3;
        final short backA = 4;
        final short backB = 5;
//      final short backC;
        final short backD = 6;

        short[] FRONT = new short[]{frontA, frontB, frontC, frontD};
        short[] RIGHT = new short[]{frontB, backB, frontD, backD};
        short[] TOP = new short[]{backA, backB, frontA, frontB};

        short[] data = new short[18];

        int offset = 0;
        for (short[] face : asList(FRONT, RIGHT, TOP)) {
            data[offset++] = face[0];
            data[offset++] = face[2];
            data[offset++] = face[1];
            data[offset++] = face[2];
            data[offset++] = face[3];
            data[offset++] = face[1];
        }
        return data;
    }

    private float[] buildVertexData(Point3D position, Color color, float width, float height, float depth) {
        //@formatter:off
        final ColorPoint3D frontA = new ColorPoint3D(new Point3D(position.x,         position.y + height, position.z + depth), color);
        final ColorPoint3D frontB = new ColorPoint3D(new Point3D(position.x + width, position.y + height, position.z + depth), color);
        final ColorPoint3D frontC = new ColorPoint3D(new Point3D(position.x,         position.y,          position.z + depth), color);
        final ColorPoint3D frontD = new ColorPoint3D(new Point3D(position.x + width, position.y,          position.z + depth), color);
        final ColorPoint3D backA  = new ColorPoint3D(new Point3D(position.x,         position.y + height, position.z), color);
        final ColorPoint3D backB  = new ColorPoint3D(new Point3D(position.x + width, position.y + height, position.z), color);
//      final ColorPoint3D backC  = new ColorPoint3D(new Point3D(position.x,         position.y,          position.z), color);
        final ColorPoint3D backD  = new ColorPoint3D(new Point3D(position.x + width, position.y,          position.z), color);
        //@formatter:on

        List<ColorPoint3D> points = asList(frontA, frontB, frontC, frontD, backA, backB, backD);

        float[] data = new float[points.size() * (POSITION_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS)];
        int offset = 0;
        for (ColorPoint3D colorPoint3D : points) {
            data[offset++] = colorPoint3D.point.x;
            data[offset++] = colorPoint3D.point.y;
            data[offset++] = colorPoint3D.point.z;
            data[offset++] = colorPoint3D.color.red;
            data[offset++] = colorPoint3D.color.green;
            data[offset++] = colorPoint3D.color.blue;
            data[offset++] = colorPoint3D.color.alpha;
        }
        return data;
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
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_SHORT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    void release() {
        final int[] buffersToDelete = new int[]{vboBufferIndex, iboBufferIndex};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}