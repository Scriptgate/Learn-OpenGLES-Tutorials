package com.learnopengles.android.common;

import java.util.ArrayList;
import java.util.List;

public class CubeDataFactory {

    public static float[] generateColorData(Color front,
                                            Color right,
                                            Color back,
                                            Color left,
                                            Color top,
                                            Color bottom) {
        List<Face> faces = new ArrayList<>();
        faces.add(new ColorFace(front));
        faces.add(new ColorFace(right));
        faces.add(new ColorFace(back));
        faces.add(new ColorFace(left));
        faces.add(new ColorFace(top));
        faces.add(new ColorFace(bottom));

        return generateData(faces);
    }

    public static float[] generateColorData(Color frontA,
                                            Color frontB,
                                            Color frontC,
                                            Color frontD,
                                            Color backA,
                                            Color backB,
                                            Color backC,
                                            Color backD) {
        List<Face> faces = new ArrayList<>();
        //@formatter:off
        ColorFace FRONT  = new ColorFace(frontA, frontB, frontC, frontD);
        ColorFace RIGHT  = new ColorFace(frontB,  backB, frontD,  backD);
        ColorFace BACK   = new ColorFace( backB,  backA,  backD,  backC);
        ColorFace LEFT   = new ColorFace( backA, frontA,  backC, frontC);
        ColorFace TOP    = new ColorFace( backA,  backB, frontA, frontB);
        ColorFace BOTTOM = new ColorFace( backD,  backC, frontD, frontC);
        //@formatter:on

        faces.add(FRONT);
        faces.add(RIGHT);
        faces.add(BACK);
        faces.add(LEFT);
        faces.add(TOP);
        faces.add(BOTTOM);

        return generateData(faces);
    }

    public static float[] generateNormalData(Point3D front,
                                            Point3D right,
                                            Point3D back,
                                            Point3D left,
                                            Point3D top,
                                            Point3D bottom) {
        List<Face> faces = new ArrayList<>();
        faces.add(new PointFace(front));
        faces.add(new PointFace(right));
        faces.add(new PointFace(back));
        faces.add(new PointFace(left));
        faces.add(new PointFace(top));
        faces.add(new PointFace(bottom));

        return generateData(faces);
    }

    public static float[] generateNormalData() {
        Point3D front = new Point3D(0.0f, 0.0f, 1.0f);
        Point3D right = new Point3D(1.0f, 0.0f, 0.0f);
        Point3D back = new Point3D(0.0f, 0.0f, -1.0f);
        Point3D left = new Point3D(-1.0f, 0.0f, 0.0f);
        Point3D top = new Point3D(0.0f, 1.0f, 0.0f);
        Point3D bottom = new Point3D(0.0f, -1.0f, 0.0f);
        return generateNormalData(front, right, back, left, top, bottom);
    }

    public static float[] generatePositionData(Point3D frontA,
                                               Point3D frontB,
                                               Point3D frontC,
                                               Point3D frontD,
                                               Point3D backA,
                                               Point3D backB,
                                               Point3D backC,
                                               Point3D backD) {
        List<Face> faces = new ArrayList<>();
        //@formatter:off
        PointFace FRONT  = new PointFace(frontA, frontB, frontC, frontD);
        PointFace RIGHT  = new PointFace(frontB,  backB, frontD,  backD);
        PointFace BACK   = new PointFace( backB,  backA,  backD,  backC);
        PointFace LEFT   = new PointFace( backA, frontA,  backC, frontC);
        PointFace TOP    = new PointFace( backA,  backB, frontA, frontB);
        PointFace BOTTOM = new PointFace( backD,  backC, frontD, frontC);
        //@formatter:on

        faces.add(FRONT);
        faces.add(RIGHT);
        faces.add(BACK);
        faces.add(LEFT);
        faces.add(TOP);
        faces.add(BOTTOM);

        return generateData(faces);
    }


    private static float[] generateData(List<Face> faces) {
        final int size = faces.iterator().next().getNumberOfElements() * 6 * 6;
        final float[] cubeData = new float[size];

        for (int faceIndex = 0; faceIndex < 6; faceIndex++) {
            final Face face = faces.get(faceIndex);
            int offset = faceIndex * face.getNumberOfElements() * 6;

            face.addFaceToArray(cubeData, offset);
        }

        return cubeData;
    }

    public static float[] generatePositionData(float width, float height, float depth) {
        //@formatter:off
        final Point3D frontA = new Point3D(-width,  height,  depth);
        final Point3D frontB = new Point3D( width,  height,  depth);
        final Point3D frontC = new Point3D(-width, -height,  depth);
        final Point3D frontD = new Point3D( width, -height,  depth);
        final Point3D backA  = new Point3D(-width,  height, -depth);
        final Point3D backB  = new Point3D( width,  height, -depth);
        final Point3D backC  = new Point3D(-width, -height, -depth);
        final Point3D backD  = new Point3D( width, -height, -depth);
        //@formatter:on
        return generatePositionData(frontA, frontB, frontC, frontD, backA, backB, backC, backD);
    }

    private abstract static class Face<T> {
        private final T p1;
        private final T p2;
        private final T p3;
        private final T p4;

        private Face(T p1, T p2, T p3, T p4) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.p4 = p4;
        }

        void addFaceToArray(float[] data, int offset) {
            // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
            // if the points are counter-clockwise we are looking at the "front". If not we are looking at
            // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
            // usually represent the backside of an object and aren't visible anyways.

            int numberOfElements = getNumberOfElements();

            // Build the triangles
            //  1---3,6
            //  | / |
            // 2,4--5
            addToArray(p1, data, offset);
            addToArray(p3, data, offset + numberOfElements);
            addToArray(p2, data, offset + numberOfElements * 2);
            addToArray(p3, data, offset + numberOfElements * 3);
            addToArray(p4, data, offset + numberOfElements * 4);
            addToArray(p2, data, offset + numberOfElements * 5);
        }

        abstract int getNumberOfElements();

        abstract void addToArray(T element, float[] data, int offset);
    }

    private static class PointFace extends Face<Point3D> {

        private PointFace(Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
            super(p1, p2, p3, p4);
        }

        private PointFace(Point3D face) {
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

    private static class ColorFace extends Face<Color> {
        public ColorFace(Color p1, Color p2, Color p3, Color p4) {
            super(p1, p2, p3, p4);
        }

        public ColorFace(Color face) {
            this(face, face, face, face);
        }

        @Override
        int getNumberOfElements() {
            return 4;
        }

        @Override
        void addToArray(Color element, float[] data, int offset) {
            data[offset] = element.red;
            data[offset + 1] = element.green;
            data[offset + 2] = element.blue;
            data[offset + 3] = element.alpha;
        }
    }
}
