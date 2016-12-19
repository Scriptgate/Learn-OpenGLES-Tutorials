package com.learnopengles.android.lesson4;


import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.cube.data.type.ColorCubeData;
import com.learnopengles.android.cube.data.type.NormalCubeData;
import com.learnopengles.android.cube.data.type.PositionCubeData;
import com.learnopengles.android.cube.data.type.TextureCubeData;

import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.CYAN;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.MAGENTA;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.Color.YELLOW;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.CubeDataFactory.generateTextureData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.cube.data.type.CubeDataType.COLOR;
import static com.learnopengles.android.cube.data.type.CubeDataType.NORMAL;
import static com.learnopengles.android.cube.data.type.CubeDataType.POSITION;
import static com.learnopengles.android.cube.data.type.CubeDataType.TEXTURE;

public class CubeData {

    private final CubeDataCollection cubeDataCollection;

    public CubeData() {
        final float[] cubePositionData = generatePositionData(1.0f, 1.0f, 1.0f);
        final float[] cubeColorData = generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA);
        final float[] cubeNormalData = generateNormalData();
        final float[] cubeTextureCoordinateData = generateTextureData();

        cubeDataCollection = cubeData()
                .addData(POSITION, new PositionCubeData(cubePositionData))
                .addData(COLOR, new ColorCubeData(cubeColorData))
                .addData(NORMAL, new NormalCubeData(cubeNormalData))
                .addData(TEXTURE, new TextureCubeData(cubeTextureCoordinateData))
                .build();
    }

    public void passPositionTo(int handle) {
        cubeDataCollection.passTo(POSITION, handle);
    }

    public void passColorTo(int handle) {
        cubeDataCollection.passTo(COLOR, handle);
    }

    public void passNormalTo(int handle) {
        cubeDataCollection.passTo(NORMAL, handle);
    }

    public void passTextureTo(int handle) {
        cubeDataCollection.passTo(TEXTURE, handle);
    }
}
