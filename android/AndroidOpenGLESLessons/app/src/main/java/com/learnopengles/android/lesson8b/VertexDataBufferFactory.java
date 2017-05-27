package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Point2D;
import com.learnopengles.android.common.Point3D;

import java.nio.FloatBuffer;
import java.util.List;

import static com.learnopengles.android.common.BufferHelper.putIn;
import static com.learnopengles.android.lesson8b.IndexBufferObject.allocatePositionDataBuffer;
import static com.learnopengles.android.lesson8b.IndexBufferObject.allocateTextureDataBuffer;
import static java.util.Arrays.asList;

public class VertexDataBufferFactory {

    static FloatBuffer createPositionData(int numberOfCubes, Point3D offset) {
        FloatBuffer positionDataBuffer = allocatePositionDataBuffer(numberOfCubes);

        Point3D position = new Point3D(offset.x, offset.y, offset.z);

        for (int i = 0; i < numberOfCubes; i++) {
            for (Point3D point : buildVertices(position)) {
                putIn(positionDataBuffer, point);
            }
            position.y += 0.2f;
        }
        positionDataBuffer.position(0);
        return positionDataBuffer;
    }

    private static List<Point3D> buildVertices(Point3D position) {
        float width = 1;
        float height = 0.2f;
        float depth = 1;
        //@formatter:off
        final Point3D frontA = new Point3D(position.x,         position.y + height, position.z + depth);
        final Point3D frontB = new Point3D(position.x + width, position.y + height, position.z + depth);
        final Point3D frontC = new Point3D(position.x,         position.y,          position.z + depth);
        final Point3D frontD = new Point3D(position.x + width, position.y,          position.z + depth);
        final Point3D backA  = new Point3D(position.x,         position.y + height, position.z);
        final Point3D backB  = new Point3D(position.x + width, position.y + height, position.z);
        final Point3D backD  = new Point3D(position.x + width, position.y,          position.z);
        //@formatter:on

        return asList(frontA, frontB, frontC, frontD, backA, backB, backD);
    }

    static FloatBuffer createTextureData(int numberOfCubes, int indexOffset) {
        FloatBuffer textureDataBuffer = allocateTextureDataBuffer(numberOfCubes);

        for (int i = 0; i < numberOfCubes; i++) {
            TextureTriangle texture = new TextureTriangle(i + indexOffset);
            for (Point2D point : buildVertices(texture)) {
                putIn(textureDataBuffer, point);
            }
        }
        textureDataBuffer.position(0);
        return textureDataBuffer;
    }

    private static List<Point2D> buildVertices(TextureTriangle texture) {
        final Point2D frontA = texture.p1;
        final Point2D frontB = texture.p2;
        final Point2D frontC = texture.p3;
        final Point2D frontD = texture.p1;
        final Point2D backA = texture.p3;
        final Point2D backB = texture.p1;
        final Point2D backD = texture.p3;

        return asList(frontA, frontB, frontC, frontD, backA, backB, backD);
    }
}
