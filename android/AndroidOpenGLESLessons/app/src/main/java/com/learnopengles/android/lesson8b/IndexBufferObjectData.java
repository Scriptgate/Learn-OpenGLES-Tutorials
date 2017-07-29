package com.learnopengles.android.lesson8b;


import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

class IndexBufferObjectData {
    final FloatBuffer positionDataBuffer;
    final FloatBuffer textureDataBuffer;
    final ShortBuffer indexBuffer;
    final int numberOfCubes;

    IndexBufferObjectData(FloatBuffer positionDataBuffer, FloatBuffer textureDataBuffer, ShortBuffer indexBuffer, int numberOfCubes) {

        this.positionDataBuffer = positionDataBuffer;
        this.textureDataBuffer = textureDataBuffer;
        this.indexBuffer = indexBuffer;
        this.numberOfCubes = numberOfCubes;
    }

    void release() {
        positionDataBuffer.limit(0);
        textureDataBuffer.limit(0);
        indexBuffer.limit(0);
    }
}
