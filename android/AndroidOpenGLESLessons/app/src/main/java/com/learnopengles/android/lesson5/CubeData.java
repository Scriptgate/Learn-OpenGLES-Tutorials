package com.learnopengles.android.lesson5;

import com.learnopengles.android.cube.data.CubeDataCollection;

import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.cube.CubeDataFactory.*;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;

public class CubeData {

    private final CubeDataCollection cubeDataCollection;

    public CubeData() {
        cubeDataCollection = cubeData()
                .positions(generatePositionData(1.0f, 1.0f, 1.0f))
                .colors(generateColorData(RED, MAGENTA, BLACK, BLUE, YELLOW, WHITE, GREEN, CYAN))
                .build();
    }

    public void passPositionTo(int handle) {
        cubeDataCollection.passPositionTo(handle);
    }

    public void passColorTo(int handle) {
        cubeDataCollection.passColorTo(handle);
    }
}
