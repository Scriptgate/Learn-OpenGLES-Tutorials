package com.learnopengles.android.cube.renderer.data;


import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;

public class CubeDataRendererFactory {

    public static CubeDataRenderer positionCubeRenderer() {
        return new CubeDataRenderer(CubeDataType.POSITION, AttributeVariable.POSITION);
    }

    public static CubeDataRenderer colorCubeRenderer() {
        return new CubeDataRenderer(CubeDataType.COLOR, AttributeVariable.COLOR);
    }

    public static CubeDataRenderer normalCubeRenderer() {
        return new CubeDataRenderer(CubeDataType.NORMAL, AttributeVariable.NORMAL);
    }

    public static CubeDataRenderer textureCoordinateCubeRenderer() {
        return new CubeDataRenderer(CubeDataType.TEXTURE_COORDINATE, AttributeVariable.TEXTURE_COORDINATE);
    }
}
