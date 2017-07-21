package com.learnopengles.android.lesson5;

import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeData;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;

class CubeRenderer {

    private final Program program;
    private final ModelMatrix modelMatrix;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final ModelViewProjectionMatrix mvpMatrix;

    CubeRenderer(Program program, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix) {
        this.program = program;
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mvpMatrix = mvpMatrix;
    }

    void draw(Cube cube) {
        cube.apply(modelMatrix);

        passCubeDataToAttribute(cube, CubeDataType.POSITION, AttributeVariable.POSITION);
        passCubeDataToAttribute(cube, CubeDataType.COLOR, AttributeVariable.COLOR);

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    private void passCubeDataToAttribute(Cube cube, CubeDataType cubeDataType, AttributeVariable attributeVariable) {
        CubeData cubeData = cube.getCubeData(cubeDataType);
        int handle = program.getHandle(attributeVariable);

        FloatBuffer data = cubeData.getData();
        data.position(0);
        glVertexAttribPointer(handle, cubeData.getDataSize(), GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }
}
