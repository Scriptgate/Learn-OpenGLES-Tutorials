package com.learnopengles.android.lesson8b;


import java.nio.ShortBuffer;

import static com.learnopengles.android.common.BufferHelper.allocateShortBuffer;
import static java.util.Arrays.asList;

public class IndexDataBufferFactory {

    static ShortBuffer createIndexData(int numberOfCubes) {

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

}
