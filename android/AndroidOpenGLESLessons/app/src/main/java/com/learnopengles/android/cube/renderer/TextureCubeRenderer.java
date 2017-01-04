package com.learnopengles.android.cube.renderer;

import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.data.CubeData;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;

public class TextureCubeRenderer implements CubeRenderer {

    private final Program program;

    public TextureCubeRenderer(Program program) {
        this.program = program;
    }

    @Override
    public void apply(Cube cube) {
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, cube.getTexture());
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(program.getHandle(TEXTURE), 0);

        CubeDataType cubeDataType = CubeDataType.TEXTURE_COORDINATE;
        int handle = program.getHandle(AttributeVariable.TEXTURE_COORDINATE);

        CubeData cubeData = cube.getCubeData(cubeDataType);
        FloatBuffer data = cubeData.getData();
        int dataSize = cubeData.getDataSize();

        data.position(0);
        glVertexAttribPointer(handle, dataSize, GL_FLOAT, false, 0, data);
        glEnableVertexAttribArray(handle);
    }
}
