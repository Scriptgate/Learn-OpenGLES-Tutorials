package com.learnopengles.android.cube.data;


import com.learnopengles.android.cube.data.type.CubeDataType;

import java.util.HashMap;
import java.util.Map;

public class CubeDataCollectionBuilder {

    private Map<CubeDataType, CubeData> cubeData;

    public static CubeDataCollectionBuilder cubeData() {
        return new CubeDataCollectionBuilder();
    }

    private CubeDataCollectionBuilder() {
        cubeData = new HashMap<>();
    }

    public CubeDataCollectionBuilder addData(CubeDataType type, CubeData data) {
        cubeData.put(type, data);
        return this;
    }

    public CubeDataCollection build() {
        return new CubeDataCollection(cubeData);
    }

}
