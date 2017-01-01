package com.learnopengles.android.cube.data;


import java.util.HashMap;
import java.util.Map;

import static com.learnopengles.android.cube.data.CubeDataType.*;

public class CubeDataCollectionBuilder {

    private static final int POSITION_DATA_SIZE = 3;
    private static final int COLOR_DATA_SIZE = 4;
    private static final int NORMAL_DATA_SIZE = 3;
    private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;

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

    public CubeDataCollectionBuilder positions(float[] positionData) {
        return addData(POSITION, new CubeData(positionData, POSITION_DATA_SIZE));
    }

    public CubeDataCollectionBuilder colors(float[] colorData) {
        return addData(COLOR, new CubeData(colorData, COLOR_DATA_SIZE));
    }

    public CubeDataCollectionBuilder normals(float[] normalData) {
        return addData(NORMAL, new CubeData(normalData, NORMAL_DATA_SIZE));
    }

    public CubeDataCollectionBuilder textures(float[] textureData) {
        return addData(TEXTURE_COORDINATE, new CubeData(textureData, TEXTURE_COORDINATE_DATA_SIZE));
    }

    public CubeDataCollection build() {
        return new CubeDataCollection(cubeData);
    }

}
