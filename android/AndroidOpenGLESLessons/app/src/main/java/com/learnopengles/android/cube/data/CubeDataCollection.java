package com.learnopengles.android.cube.data;

import com.learnopengles.android.program.AttributeVariable;

import java.util.Map;

public class CubeDataCollection {

    private final Map<AttributeVariable, CubeData> cubeData;

    CubeDataCollection(Map<AttributeVariable, CubeData> cubeData) {
        this.cubeData = cubeData;
    }

    public CubeData getCubeData(AttributeVariable type) {
        return cubeData.get(type);
    }
}
