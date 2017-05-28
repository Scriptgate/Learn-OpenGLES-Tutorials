package com.learnopengles.android.lesson8b;


import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import java8.util.function.Function;
import java8.util.function.Supplier;

import static com.learnopengles.android.lesson8b.IndexDataBufferFactory.createIndexData;
import static com.learnopengles.android.lesson8b.VertexDataBufferFactory.createPositionData;
import static com.learnopengles.android.lesson8b.VertexDataBufferFactory.createTextureData;

public class IndexBufferObjectCreator {

    private final Supplier<FloatBuffer> positionDataBufferSupplier;
    private final Supplier<FloatBuffer> textureDataBufferSupplier;
    private final Function<Short, ShortBuffer> indexBufferSupplier;
    public final int numberOfCubes;

    public IndexBufferObjectCreator(final List<Cube> cubes) {
        positionDataBufferSupplier = new Supplier<FloatBuffer>() {
            @Override
            public FloatBuffer get() {return createPositionData(cubes);
            }
        };
        textureDataBufferSupplier = new Supplier<FloatBuffer>() {
            @Override
            public FloatBuffer get() {return createTextureData(cubes);
            }
        };
        indexBufferSupplier = new Function<Short, ShortBuffer>() {
            @Override
            public ShortBuffer apply(Short offset) {return createIndexData(cubes, offset);
            }
        };
        this.numberOfCubes = cubes.size();
    }

    public IndexBufferObjectData createData(short indexOffset) {
        FloatBuffer positionDataBuffer = positionDataBufferSupplier.get();
        FloatBuffer textureDataBuffer = textureDataBufferSupplier.get();
        ShortBuffer indexBuffer = indexBufferSupplier.apply(indexOffset);
        return new IndexBufferObjectData(positionDataBuffer, textureDataBuffer, indexBuffer, numberOfCubes);
    }
}

