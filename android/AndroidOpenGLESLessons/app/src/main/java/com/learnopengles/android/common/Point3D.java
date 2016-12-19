package com.learnopengles.android.common;

public class Point3D {

    public float x;
    public float y;
    public float z;

    public Point3D() {
        this(0, 0, 0);
    }

    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}