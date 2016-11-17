package com.learnopengles.android.common;

public class PointBuilder {

    private final Point prototype;

    public PointBuilder(Point prototype) {
        this.prototype = prototype;
    }

    public Point xy(float x, float y) {
        return new Point(x, y, prototype.z);
    }

    public Point yz(float y, float z) {
        return new Point(prototype.x, y, z);
    }

    public Point xz(float x, float z) {
        return new Point(x, prototype.y, z);
    }


}
