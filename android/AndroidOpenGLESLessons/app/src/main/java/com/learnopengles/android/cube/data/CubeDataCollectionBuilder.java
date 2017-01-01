package com.learnopengles.android.cube.data;


import java.util.HashMap;
import java.util.Map;

import static com.learnopengles.android.cube.data.CubeDataType.*;

public class CubeDataCollectionBuilder {

    private Map<CubeDataType, CubeData> cubeData;

    public static CubeDataCollectionBuilder cubeData() {
        return new CubeDataCollectionBuilder();
    }

    private CubeDataCollectionBuilder() {
        cubeData = new HashMap<>();
    }

    public CubeDataCollectionBuilder addData(CubeDataType type, float[] data) {
        cubeData.put(type, new CubeData(data, type.size()));
        return this;
    }

    public CubeDataCollectionBuilder positions(float[] positionData) {
        return addData(POSITION, positionData);
    }

    public CubeDataCollectionBuilder colors(float[] colorData) {
        return addData(COLOR, colorData);
    }

    public CubeDataCollectionBuilder normals(float[] normalData) {
        return addData(NORMAL, normalData);
    }

    public CubeDataCollectionBuilder textures(float[] textureData) {
        return addData(TEXTURE_COORDINATE, textureData);
    }

    public CubeDataCollection build() {
        return new CubeDataCollection(cubeData);
    }

}
