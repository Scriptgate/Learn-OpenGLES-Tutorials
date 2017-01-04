package com.learnopengles.android.cube.data;

import java.util.Map;

public class CubeDataCollection {

    private final Map<CubeDataType, CubeData> cubeData;

    CubeDataCollection(Map<CubeDataType, CubeData> cubeData) {
        this.cubeData = cubeData;
    }

    public CubeData getCubeData(CubeDataType type) {
        return cubeData.get(type);
    }
}
