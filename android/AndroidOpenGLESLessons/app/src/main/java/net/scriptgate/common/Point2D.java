package net.scriptgate.common;

public class Point2D {

    private float x;
    private float y;

    public Point2D() {
        this(0, 0);
    }

    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
}
