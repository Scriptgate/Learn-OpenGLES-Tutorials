package com.learnopengles.android.cube.data;


public enum CubeDataType {
    POSITION(3),
    COLOR(4),
    NORMAL(3),
    TEXTURE_COORDINATE(2);

    private int dataSize;

    CubeDataType(int dataSize) {
        this.dataSize = dataSize;
    }

    public int size() {
        return dataSize;
    }
}
