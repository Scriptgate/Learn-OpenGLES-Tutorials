package net.scriptgate.common;

public class ColorPoint3D {

    private final Point3D point;
    private final Color color;

    public ColorPoint3D(Point3D point, Color color) {
        this.point = point;
        this.color = color;
    }

    public float x() {
        return point.x();
    }

    public float y() {
        return point.y();
    }

    public float z() {
        return point.z();
    }

    public float red() {
        return color.red();
    }

    public float green() {
        return color.green();
    }

    public float blue() {
        return color.blue();
    }

    public float alpha() {
        return color.alpha();
    }
}
