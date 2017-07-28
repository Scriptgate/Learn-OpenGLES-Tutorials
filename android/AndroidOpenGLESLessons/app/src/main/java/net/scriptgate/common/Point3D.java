package net.scriptgate.common;

public class Point3D {

    private final float x;
    private final float y;
    private final float z;

    public Point3D() {
        this(0, 0, 0);
    }

    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public Point3D x(float x) {
        return new Point3D(x, y(), z());
    }

    public Point3D y(float y) {
        return new Point3D(x(), y, z());
    }

    public Point3D z(float z) {
        return new Point3D(x(), y(), z);
    }
}
