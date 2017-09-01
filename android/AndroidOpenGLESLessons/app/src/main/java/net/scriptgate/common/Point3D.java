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

    public static Point3D minus(Point3D u, Point3D v) {
        return new Point3D(u.x - v.x, u.y - v.y, u.z - v.z);
    }

    public static Point3D crossProduct(Point3D u, Point3D v) {
        return new Point3D(
                (u.y * v.z) - (v.y * u.z),
                (v.x * u.z) - (u.x * v.z),
                (u.x * v.y) - (v.x * u.y)
        );
    }

    // dot product (3D) which allows vector operations in arguments
    public static float dot(Point3D u, Point3D v) {
        return ((u.x * v.x) + (u.y * v.y) + (u.z * v.z));
    }

    //scalar product
    public static Point3D scalarProduct(float r, Point3D u) {
        return new Point3D(u.x * r, u.y * r, u.z * r);
    }

    public static Point3D addition(Point3D u, Point3D v) {
        return new Point3D(u.x + v.x, u.y + v.y, u.z + v.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point3D point3D = (Point3D) o;

        if (Float.compare(point3D.x, x) != 0) return false;
        if (Float.compare(point3D.y, y) != 0) return false;
        return Float.compare(point3D.z, z) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }
}
