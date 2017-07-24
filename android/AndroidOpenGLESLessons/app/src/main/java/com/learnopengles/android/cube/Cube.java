package com.learnopengles.android.cube;


import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.program.AttributeVariable;

import java.nio.FloatBuffer;
import java.util.Map;

public class Cube {

    private final Map<AttributeVariable, FloatBuffer> data;

    private Point3D position = new Point3D();
    private Point3D rotation = new Point3D();
    private Point3D scale = new Point3D(1.0f, 1.0f, 1.0f);

    private int texture;

    public Cube(Map<AttributeVariable, FloatBuffer> data) {
        this.data = data;
    }

    public Cube(Map<AttributeVariable, FloatBuffer> data, Point3D position) {
        this.data = data;
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

    public FloatBuffer getData(AttributeVariable cubeDataType) {
        //TODO: because the position method returns a Buffer, we can't simply return data.get(cubeDataType).position(0). Perhaps the type should be Buffer instead?
        FloatBuffer floatBuffer = data.get(cubeDataType);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public int getTexture() {
        return texture;
    }
}
