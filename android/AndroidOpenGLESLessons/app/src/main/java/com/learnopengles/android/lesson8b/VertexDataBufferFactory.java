package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Point2D;
import com.learnopengles.android.common.Point3D;

import java.nio.FloatBuffer;
import java.util.List;

import java8.util.function.Function;
import java8.util.function.IntFunction;
import java8.util.stream.IntStream;
import java8.util.stream.Stream;

import static com.learnopengles.android.common.BufferHelper.putPoint2DIn;
import static com.learnopengles.android.common.BufferHelper.putPoint3DIn;
import static com.learnopengles.android.lesson8b.Cube.toColorIndex;
import static com.learnopengles.android.lesson8b.Cube.toPosition;
import static com.learnopengles.android.lesson8b.IndexBufferObject.allocatePositionDataBuffer;
import static com.learnopengles.android.lesson8b.IndexBufferObject.allocateTextureDataBuffer;
import static com.learnopengles.android.lesson8b.TextureTriangle.toTextureTriangle;
import static java.util.Arrays.asList;
import static java8.util.stream.IntStreams.range;
import static java8.util.stream.StreamSupport.stream;

class VertexDataBufferFactory {

    static FloatBuffer createPositionData(List<Cube> cubes) {
        Stream<Point3D> positionData = stream(cubes).map(toPosition());

        return createPositionData(positionData, cubes.size());
    }

    static FloatBuffer createPositionData(int numberOfCubes, final Point3D offset) {
        Stream<Point3D> positionData = range(0, numberOfCubes)
                .mapToObj(new IntFunction<Point3D>() {
                    @Override
                    public Point3D apply(int value) {
                        return new Point3D(offset.x, offset.y + value * 0.2f, offset.z);
                    }
                });

        return createPositionData(positionData, numberOfCubes);
    }

    private static FloatBuffer createPositionData(Stream<Point3D> positions, int numberOfCubes) {
        FloatBuffer positionDataBuffer = allocatePositionDataBuffer(numberOfCubes);

        positions.flatMap(positionToVertices())
                .forEach(putPoint3DIn(positionDataBuffer));

        positionDataBuffer.position(0);
        return positionDataBuffer;
    }

    private static Function<Point3D, Stream<Point3D>> positionToVertices() {
        final float width = 1;
        final float height = 0.2f;
        final float depth = 1;
        return new Function<Point3D, Stream<Point3D>>() {
            @Override
            public Stream<Point3D> apply(Point3D position) {
                //@formatter:off
                final Point3D frontA = new Point3D(position.x,         position.y + height, position.z + depth);
                final Point3D frontB = new Point3D(position.x + width, position.y + height, position.z + depth);
                final Point3D frontC = new Point3D(position.x,         position.y,          position.z + depth);
                final Point3D frontD = new Point3D(position.x + width, position.y,          position.z + depth);
                final Point3D backA  = new Point3D(position.x,         position.y + height, position.z);
                final Point3D backB  = new Point3D(position.x + width, position.y + height, position.z);
                final Point3D backD  = new Point3D(position.x + width, position.y,          position.z);
                //@formatter:on

                return stream(asList(frontA, frontB, frontC, frontD, backA, backB, backD));
            }
        };
    }

    static FloatBuffer createTextureData(List<Cube> cubes) {
        IntStream indices = stream(cubes).mapToInt(toColorIndex());
        return createTextureData(indices, cubes.size());
    }

    static FloatBuffer createTextureData(int numberOfCubes, int indexOffset) {
        IntStream indices = range(indexOffset, indexOffset + numberOfCubes);
        return createTextureData(indices, numberOfCubes);
    }

    private static FloatBuffer createTextureData(IntStream indices, int numberOfCubes) {
        final FloatBuffer textureDataBuffer = allocateTextureDataBuffer(numberOfCubes);

        indices.mapToObj(toTextureTriangle())
                .flatMap(textureToVertices())
                .forEach(putPoint2DIn(textureDataBuffer));

        textureDataBuffer.position(0);
        return textureDataBuffer;
    }

    private static Function<TextureTriangle, Stream<Point2D>> textureToVertices() {
        return new Function<TextureTriangle, Stream<Point2D>>() {
            @Override
            public Stream<Point2D> apply(TextureTriangle texture) {

                final Point2D frontA = texture.p1;
                final Point2D frontB = texture.p2;
                final Point2D frontC = texture.p3;
                final Point2D frontD = texture.p1;
                final Point2D backA = texture.p3;
                final Point2D backB = texture.p1;
                final Point2D backD = texture.p3;

                return stream(asList(frontA, frontB, frontC, frontD, backA, backB, backD));
            }
        };
    }
}
