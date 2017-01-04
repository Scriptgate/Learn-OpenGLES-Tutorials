package com.learnopengles.android.cube;


import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.cube.data.CubeData;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.cube.data.CubeDataType;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

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

    public void apply(ModelMatrix modelMatrix) {
        modelMatrix.setIdentity();

        //TODO: Not all cubes have all these properties, some have separate ways to handle rotation
        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);
        modelMatrix.scale(scale);
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }

    public void setScale(Point3D scale) {
        this.scale = scale;
    }

    public CubeData getCubeData(CubeDataType cubeDataType) {
        return cubeData.getCubeData(cubeDataType);
    }

    public int getTexture() {
        return texture;
    }
}
