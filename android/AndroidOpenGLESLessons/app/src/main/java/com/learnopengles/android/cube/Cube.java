package com.learnopengles.android.cube;


import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.cube.data.CubeDataType;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1i;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;

public class Cube {

    protected final CubeDataCollection cubeData;
    protected Point3D position = new Point3D();
    protected Point3D rotation = new Point3D();
    protected Point3D scale = new Point3D(1.0f, 1.0f, 1.0f);
    private int texture;

    public Cube(CubeDataCollection cubeData) {
        this.cubeData = cubeData;
    }

    public Cube(CubeDataCollection cubeData, Point3D position) {
        this.cubeData = cubeData;
        this.position = position;
    }

    public void setRotationX(float rotation) {
        this.rotation.x = rotation;
    }

    public void setRotationY(float rotation) {
        this.rotation.y = rotation;
    }

    public void setRotationZ(float rotation) {
        this.rotation.z = rotation;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public void drawTexture(Program program) {
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, texture);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(program.getHandle(TEXTURE), 0);

        cubeData.passTo(CubeDataType.TEXTURE_COORDINATE, program.getHandle(AttributeVariable.TEXTURE_COORDINATE));
    }

    public void apply(ModelMatrix modelMatrix) {
        modelMatrix.setIdentity();

        //TODO: Not all cubes have all these properties, some have separate ways to handle rotation
        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);
        modelMatrix.scale(scale);
    }

    public void drawArrays() {
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }

    public void passTo(CubeDataType cubeDataType, int handle) {
        cubeData.passTo(cubeDataType, handle);
    }

    public void setScale(Point3D scale) {
        this.scale = scale;
    }
}
