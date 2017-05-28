package com.learnopengles.android.lesson8b;


import java.nio.ShortBuffer;
import java.util.List;

import static com.learnopengles.android.lesson8b.IndexBufferObject.allocateIndexBuffer;
import static java.util.Arrays.asList;

public class IndexDataBufferFactory {

    static ShortBuffer createIndexData(List<Cube> cubes, short indexOffset) {
        int numberOfCubes = cubes.size();
        return createIndexData(numberOfCubes, indexOffset);
    }

    static ShortBuffer createIndexData(int numberOfCubes, short indexOffset) {

        ShortBuffer indexBuffer = allocateIndexBuffer(numberOfCubes);

        short index = indexOffset;

        for (int i = 0; i < numberOfCubes; i++) {
            final short frontA = index++;
            final short frontB = index++;
            final short frontC = index++;
            final short frontD = index++;
            final short backA = index++;
            final short backB = index++;
            final short backD = index++;

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

}
