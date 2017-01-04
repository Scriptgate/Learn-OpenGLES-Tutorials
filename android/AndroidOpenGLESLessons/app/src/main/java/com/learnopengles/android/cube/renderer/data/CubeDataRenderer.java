package com.learnopengles.android.cube.renderer.data;


import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeData;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.VertexAttribPointerRenderer;

public class CubeDataRenderer extends VertexAttribPointerRenderer<Cube> {

    private CubeDataType cubeDataType;
    private AttributeVariable attributeVariable;

    public CubeDataRenderer(CubeDataType cubeDataType, AttributeVariable attributeVariable) {
        this.cubeDataType = cubeDataType;
        this.attributeVariable = attributeVariable;
    }

    public void apply(Program program, Cube cube) {
        CubeData cubeData = cube.getCubeData(cubeDataType);
        int handle = program.getHandle(attributeVariable);

        apply(handle, cubeData.getData(), cubeData.getDataSize());
    }

}
