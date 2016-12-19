package com.learnopengles.android.lesson2;

import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.cube.data.type.ColorCubeData;
import com.learnopengles.android.cube.data.type.CubeDataType;
import com.learnopengles.android.cube.data.type.NormalCubeData;
import com.learnopengles.android.cube.data.type.PositionCubeData;

import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.CYAN;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.MAGENTA;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.Color.YELLOW;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generateNormalData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static com.learnopengles.android.cube.data.CubeDataCollectionBuilder.cubeData;
import static com.learnopengles.android.cube.data.type.CubeDataType.COLOR;
import static com.learnopengles.android.cube.data.type.CubeDataType.NORMAL;
import static com.learnopengles.android.cube.data.type.CubeDataType.POSITION;

public class CubeData {

    private CubeDataCollection cubeDataCollection;

    public CubeData() {
        final float[] cubePositionData = generatePositionData(1.0f, 1.0f, 1.0f);
        final float[] cubeColorData = generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA);
        final float[] cubeNormalData = generateNormalData();

        cubeDataCollection = cubeData()
                .addData(POSITION, new PositionCubeData(cubePositionData))
                .addData(COLOR, new ColorCubeData(cubeColorData))
                .addData(NORMAL, new NormalCubeData(cubeNormalData))
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
}
