package com.learnopengles.android.cube;

import com.learnopengles.android.common.Point3D;

public class Point3DFace extends Face<Point3D> {

    Point3DFace(Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
        super(p1, p2, p3, p4);
    }

    Point3DFace(Point3D face) {
        this(face, face, face, face);
    }

    @Override
    int getNumberOfElements() {
        return 3;
    }

    @Override
    void addToArray(Point3D element, float[] data, int offset) {
        data[offset] = element.x;
        data[offset + 1] = element.y;
        data[offset + 2] = element.z;
    }
}
