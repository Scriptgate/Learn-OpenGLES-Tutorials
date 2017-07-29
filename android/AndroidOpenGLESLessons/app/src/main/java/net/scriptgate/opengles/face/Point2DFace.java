package net.scriptgate.opengles.face;

import net.scriptgate.common.Point2D;

public class Point2DFace extends Face<Point2D> {

    public Point2DFace(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        super(p1, p2, p3, p4);
    }

    public Point2DFace(Point2D face) {
        this(face, face, face, face);
    }

    @Override
    public int getNumberOfElements() {
        return 2;
    }

    @Override
    public void addToArray(Point2D point, float[] data, int offset) {
        data[offset] = point.x();
        data[offset + 1] = point.y();
    }
}