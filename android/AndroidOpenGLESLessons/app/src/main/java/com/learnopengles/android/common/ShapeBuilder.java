package com.learnopengles.android.common;

import com.learnopengles.android.lesson1.Color;

import java.util.ArrayList;
import java.util.List;

public class ShapeBuilder {

    public static float[] generateCubeData(Color frontA,
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

        return generateCubeData(faces);
    }

    public static float[] generateCubeData(Point frontA,
                                           Point frontB,
                                           Point frontC,
                                           Point frontD,
                                           Point backA,
                                           Point backB,
                                           Point backC,
                                           Point backD) {
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

        return generateCubeData(faces);
    }


    private static float[] generateCubeData(List<Face> faces) {
        final int size = faces.iterator().next().getNumberOfElements() * 6 * 6;
        final float[] cubeData = new float[size];

        for (int faceIndex = 0; faceIndex < 6; faceIndex++) {
            final Face face = faces.get(faceIndex);
            int offset = faceIndex * face.getNumberOfElements() * 6;

            face.addFaceToArray(cubeData, offset);
        }

        return cubeData;
    }

    public static float[] generateCubeData(float width, float height, float depth) {
        //@formatter:off
        final Point frontA = new Point(-width,  height,  depth);
        final Point frontB = new Point( width,  height,  depth);
        final Point frontC = new Point(-width, -height,  depth);
        final Point frontD = new Point( width, -height,  depth);
        final Point backA  = new Point(-width,  height, -depth);
        final Point backB  = new Point( width,  height, -depth);
        final Point backC  = new Point(-width, -height, -depth);
        final Point backD  = new Point( width, -height, -depth);
        //@formatter:on
        return generateCubeData(frontA, frontB, frontC, frontD, backA, backB, backC, backD);
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

    private static class PointFace extends Face<Point> {

        private PointFace(Point p1, Point p2, Point p3, Point p4) {
            super(p1, p2, p3, p4);
        }

        @Override
        int getNumberOfElements() {
            return 3;
        }

        @Override
        void addToArray(Point element, float[] data, int offset) {
            data[offset] = element.x;
            data[offset + 1] = element.y;
            data[offset + 2] = element.z;
        }

    }

    private static class ColorFace extends Face<Color> {
        public ColorFace(Color p1, Color p2, Color p3, Color p4) {
            super(p1, p2, p3, p4);
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
            data[offset + 2] = element.alpha;
        }
    }
}
