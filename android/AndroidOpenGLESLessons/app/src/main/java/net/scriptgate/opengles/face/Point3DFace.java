package net.scriptgate.opengles.face;

import net.scriptgate.common.Point3D;

public class Point3DFace extends Face<Point3D> {

    public Point3DFace(Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
        super(p1, p2, p3, p4);
    }

    public Point3DFace(Point3D face) {
        this(face, face, face, face);
    }

    @Override
    public int getNumberOfElements() {
        return 3;
    }

    @Override
    public void addToArray(Point3D element, float[] data, int offset) {
        data[offset] = element.x;
        data[offset + 1] = element.y;
        data[offset + 2] = element.z;
    }
}
