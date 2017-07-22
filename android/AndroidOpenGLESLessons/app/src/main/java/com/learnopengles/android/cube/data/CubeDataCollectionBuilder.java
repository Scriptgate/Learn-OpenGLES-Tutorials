package com.learnopengles.android.cube.data;


import com.learnopengles.android.program.AttributeVariable;

import java.util.HashMap;
import java.util.Map;

import static com.learnopengles.android.program.AttributeVariable.*;

public class CubeDataCollectionBuilder {

    private Map<AttributeVariable, CubeData> cubeData;

    public static CubeDataCollectionBuilder cubeData() {
        return new CubeDataCollectionBuilder();
    }

    private CubeDataCollectionBuilder() {
        cubeData = new HashMap<>();
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

    private CubeDataCollectionBuilder addData(AttributeVariable type, float[] data) {
        cubeData.put(type, new CubeData(data, type.getSize()));
        return this;
    }

    public CubeDataCollection build() {
        return new CubeDataCollection(cubeData);
    }

}
