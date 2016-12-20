package com.learnopengles.android.cube;


import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.UniformVariable.*;

public abstract class AbstractCube {

    protected final CubeDataCollection cubeData;
    protected Point3D position = new Point3D();
    protected Point3D rotation = new Point3D();

    public AbstractCube(CubeDataCollection cubeData, Point3D position) {
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

    public final void drawCube(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        passPositionData(program);
        passColorData(program);
        passData(program, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public final void drawCube(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light light) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        passPositionData(program);
        passColorData(program);
        passData(program, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix);

        passData(program, light);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public abstract void passData(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix);

    public void passData(Program program, Light light) {

    }

    protected void passPositionData(Program program) {
        // Pass in the position information
        int positionHandle = program.getHandle(POSITION);
        cubeData.passPositionTo(positionHandle);
    }

    protected void passColorData(Program program) {
        // Pass in the color information
        int colorHandle = program.getHandle(COLOR);
        cubeData.passColorTo(colorHandle);
    }

    protected void passNormalData(Program program) {
        // Pass in the normal information
        int normalHandle = program.getHandle(NORMAL);
        cubeData.passNormalTo(normalHandle);
    }

    protected void passTextureData(Program program) {
        // Pass in the texture coordinate information
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);
        cubeData.passTextureTo(textureCoordinateHandle);
    }

    protected void passMVPMatrix(Program program, ModelViewProjectionMatrix mvpMatrix) {
        // Pass in the combined matrix.
        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        mvpMatrix.passTo(mvpMatrixHandle);
    }

    protected void passMVMatrix(Program program, ModelViewProjectionMatrix mvpMatrix) {
        // Pass in the modelview matrix.
        int mvMatrixHandle = program.getHandle(MV_MATRIX);
        mvpMatrix.passTo(mvMatrixHandle);
    }

    protected void passLightTo(Program program, Light light) {
        // Pass in the light position in eye space.
        int lightPosHandle = program.getHandle(LIGHT_POSITION);
        light.passTo(lightPosHandle);
    }
}
