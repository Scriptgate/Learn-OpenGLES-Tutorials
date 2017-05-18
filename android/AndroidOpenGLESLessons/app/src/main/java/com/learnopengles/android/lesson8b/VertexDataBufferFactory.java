package com.learnopengles.android.lesson8b;


import com.learnopengles.android.common.Point3D;

import java.nio.FloatBuffer;

import static com.learnopengles.android.common.BufferHelper.allocateFloatBuffer;
import static java.util.Arrays.asList;

public class VertexDataBufferFactory {

    static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    static final int TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS = 2;

    private static final int VERTICES_PER_CUBE = 7;

    static FloatBuffer createVertexData(int numberOfCubes, Point3D offset, int indexOffset) {
        float width = 1;
        float height = 0.2f;
        float depth = 1;

        FloatBuffer vertexBuffer = allocateFloatBuffer(VERTICES_PER_CUBE * numberOfCubes * (POSITION_DATA_SIZE_IN_ELEMENTS + TEXTURE_COORDINATE_DATA_SIZE_IN_ELEMENTS));

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

}
