package com.learnopengles.android.cube.data;


import com.learnopengles.android.cube.data.type.*;

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

    public CubeDataCollectionBuilder addData(CubeDataType type, CubeData data) {
        cubeData.put(type, data);
        return this;
    }

    public CubeDataCollectionBuilder positions(float[] positionData) {
        return addData(POSITION, new PositionCubeData(positionData));
    }

    public CubeDataCollectionBuilder colors(float[] colorData) {
        return addData(COLOR, new ColorCubeData(colorData));
    }

    public CubeDataCollectionBuilder normals(float[] normalData) {
        return addData(NORMAL, new NormalCubeData(normalData));
    }

    public CubeDataCollectionBuilder textures(float[] textureData) {
        return addData(TEXTURE_COORDINATE, new TextureCubeData(textureData));
    }

    public CubeDataCollection build() {
        return new CubeDataCollection(cubeData);
    }

}
