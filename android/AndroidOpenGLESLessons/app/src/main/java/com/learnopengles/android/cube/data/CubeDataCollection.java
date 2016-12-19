package com.learnopengles.android.cube.data;


import com.learnopengles.android.cube.data.type.CubeDataType;

import java.util.Map;

public class CubeDataCollection {


    private final Map<CubeDataType, CubeData> cubeData;

    CubeDataCollection(Map<CubeDataType, CubeData> cubeData) {
        this.cubeData = cubeData;
    }

    public void passTo(CubeDataType type, int handle) {
        cubeData.get(type).passTo(handle);
    }
}
