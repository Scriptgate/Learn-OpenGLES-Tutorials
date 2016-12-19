package com.learnopengles.android.cube.data;


import java.util.Map;

import static com.learnopengles.android.cube.data.CubeDataType.*;

public class CubeDataCollection {


    private final Map<CubeDataType, CubeData> cubeData;

    CubeDataCollection(Map<CubeDataType, CubeData> cubeData) {
        this.cubeData = cubeData;
    }

    public void passTo(CubeDataType type, int handle) {
        cubeData.get(type).passTo(handle);
    }

    public void passPositionTo(int handle) {
        passTo(POSITION, handle);
    }

    public void passColorTo(int handle) {
        passTo(COLOR, handle);
    }

    public void passNormalTo(int handle) {
        passTo(NORMAL, handle);
    }

    public void passTextureTo(int handle) {
        passTo(TEXTURE, handle);
    }
}
