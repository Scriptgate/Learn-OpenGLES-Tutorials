package com.learnopengles.android.cube;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.program.AttributeVariable;

import java.nio.FloatBuffer;
import java.util.Map;

public class CubeFactory {

    private final Map<AttributeVariable, FloatBuffer> data;

    CubeFactory(Map<AttributeVariable, FloatBuffer> data) {
        this.data = data;
    }

    public Cube createAt(float x, float y, float z) {
        return new Cube(data, new Point3D(x, y, z));
    }
}
