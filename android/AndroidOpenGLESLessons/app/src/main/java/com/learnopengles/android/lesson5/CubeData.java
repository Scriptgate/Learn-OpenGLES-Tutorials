package com.learnopengles.android.lesson5;

import com.learnopengles.android.cube.CubeDataFactory;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.cube.data.type.ColorCubeData;
import com.learnopengles.android.cube.data.type.PositionCubeData;

import static com.learnopengles.android.common.Color.*;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.cube.data.type.CubeDataType.COLOR;
import static com.learnopengles.android.cube.data.type.CubeDataType.POSITION;

public class CubeData {

    private final CubeDataCollection cubeDataCollection;

    public CubeData() {
        final float[] cubePositionData = CubeDataFactory.generatePositionData(1.0f, 1.0f, 1.0f);
        final float[] cubeColorData = CubeDataFactory.generateColorData(RED, MAGENTA, BLACK, BLUE, YELLOW, WHITE, GREEN, CYAN);

        cubeDataCollection = cubeData()
                .addData(POSITION, new PositionCubeData(cubePositionData))
                .addData(COLOR, new ColorCubeData(cubeColorData))
                .build();
    }

    public void passPositionTo(int handle) {
        cubeDataCollection.passTo(POSITION, handle);
    }

    public void passColorTo(int handle) {
        cubeDataCollection.passTo(COLOR, handle);
    }
}
