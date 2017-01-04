package com.learnopengles.android.cube.renderer.data;


import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

public class CubeDataRendererFactory {

    public static CubeDataRenderer positionCubeRenderer(Program program) {
        return new CubeDataRenderer(CubeDataType.POSITION, program, AttributeVariable.POSITION);
    }

    public static CubeDataRenderer colorCubeRenderer(Program program) {
        return new CubeDataRenderer(CubeDataType.COLOR, program, AttributeVariable.COLOR);
    }

    public static CubeDataRenderer normalCubeRenderer(Program program) {
        return new CubeDataRenderer(CubeDataType.NORMAL, program, AttributeVariable.NORMAL);
    }

    public static CubeDataRenderer textureCoordinateCubeRenderer(Program program) {
        return new CubeDataRenderer(CubeDataType.TEXTURE_COORDINATE, program, AttributeVariable.TEXTURE_COORDINATE);
    }
}
