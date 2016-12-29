package com.learnopengles.android.cube;


import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1i;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.UniformVariable.*;

public class Cube {

    protected final CubeDataCollection cubeData;
    protected Point3D position = new Point3D();
    protected Point3D rotation = new Point3D();
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
        int textureUniformHandle = program.getHandle(TEXTURE);
        glUniform1i(textureUniformHandle, 0);

        passTextureData(program);
    }

    public void passPositionData(Program program) {
        // Pass in the position information
        int positionHandle = program.getHandle(POSITION);
        cubeData.passPositionTo(positionHandle);
    }

    public void passColorData(Program program) {
        // Pass in the color information
        int colorHandle = program.getHandle(COLOR);
        cubeData.passColorTo(colorHandle);
    }

    public void passNormalData(Program program) {
        // Pass in the normal information
        int normalHandle = program.getHandle(NORMAL);
        cubeData.passNormalTo(normalHandle);
    }

    public void passTextureData(Program program) {
        // Pass in the texture coordinate information
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);
        cubeData.passTextureTo(textureCoordinateHandle);
    }

    public void passMVPMatrix(Program program, ModelViewProjectionMatrix mvpMatrix) {
        // Pass in the combined matrix.
        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        mvpMatrix.passTo(mvpMatrixHandle);
    }

    public void passMVMatrix(Program program, ModelViewProjectionMatrix mvpMatrix) {
        // Pass in the modelview matrix.
        int mvMatrixHandle = program.getHandle(MV_MATRIX);
        mvpMatrix.passTo(mvMatrixHandle);
    }

    public void passLightTo(Program program, Light light) {
        // Pass in the light position in eye space.
        int lightPosHandle = program.getHandle(LIGHT_POSITION);
        light.passTo(lightPosHandle);
    }

    public void apply(ModelMatrix modelMatrix) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);
    }

    public void drawArrays() {
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }
}
