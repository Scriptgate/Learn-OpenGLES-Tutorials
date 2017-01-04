package com.learnopengles.android.cube.renderer.data;


import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeData;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.cube.renderer.CubeRenderer;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.EnabledVertexAttributeRenderer;

import java.nio.FloatBuffer;

public abstract class CubeDataRenderer implements CubeRenderer {

    protected void apply(Cube cube, CubeDataType cubeDataType, Program program, AttributeVariable attributeVariable) {
        CubeData cubeData = cube.getCubeData(cubeDataType);
        FloatBuffer data = cubeData.getData();
        int dataSize = cubeData.getDataSize();

        new EnabledVertexAttributeRenderer<Cube>(program, attributeVariable, data, dataSize).apply(cube);
    }

}
