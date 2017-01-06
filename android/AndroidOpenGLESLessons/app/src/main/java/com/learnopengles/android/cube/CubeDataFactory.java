package com.learnopengles.android.cube;

import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point2D;
import com.learnopengles.android.common.Point3D;

import java.util.ArrayList;
import java.util.List;

public class CubeDataFactory {

    private static final int FACES_PER_CUBE = 6;

    public static float[] generateColorData(Color color) {
        List<Face> faces = new ArrayList<>();
        for (int i = 0; i < FACES_PER_CUBE; i++) {
            faces.add(new ColorFace(color));
        }

        return generateData(faces);
    }

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
        faces.add(new Point3DFace(front));
        faces.add(new Point3DFace(right));
        faces.add(new Point3DFace(back));
        faces.add(new Point3DFace(left));
        faces.add(new Point3DFace(top));
        faces.add(new Point3DFace(bottom));

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
        Point3DFace FRONT  = new Point3DFace(frontA, frontB, frontC, frontD);
        Point3DFace RIGHT  = new Point3DFace(frontB,  backB, frontD,  backD);
        Point3DFace BACK   = new Point3DFace( backB,  backA,  backD,  backC);
        Point3DFace LEFT   = new Point3DFace( backA, frontA,  backC, frontC);
        Point3DFace TOP    = new Point3DFace( backA,  backB, frontA, frontB);
        Point3DFace BOTTOM = new Point3DFace( backD,  backC, frontD, frontC);
        //@formatter:on

        faces.add(FRONT);
        faces.add(RIGHT);
        faces.add(BACK);
        faces.add(LEFT);
        faces.add(TOP);
        faces.add(BOTTOM);

        return generateData(faces);
    }

    public static float[] generateCubeTextureData() {
        //@formatter:off
        //
        // The cube texture is ordered as follows:
        //     _________________
        //    |     |     |     |
        //    |  1  |  2  |  3  |
        //    |_____|_____|_____|
        //    |     |     |     |
        //    |  4  |  5  |  6  |
        //    |_____|_____|_____|
        //
        //@formatter:on
        final float ONE_THIRD = 1.0f / 3.0f;

        List<Face> faces = new ArrayList<>();
        for (int i = 0; i < FACES_PER_CUBE; i++) {

            float yOffset = i < 3 ? 0.0f : 0.5f;
            float xOffset = (i % 3) * ONE_THIRD;
            faces.add(new Point2DFace(
                            new Point2D(xOffset, yOffset),
                            new Point2D(xOffset + ONE_THIRD, yOffset),
                            new Point2D(xOffset, yOffset + 0.5f),
                            new Point2D(xOffset + ONE_THIRD, yOffset + 0.5f)
                    )
            );
        }

        return generateData(faces);
    }

    public static float[] generateTextureData() {
        return generateTextureData(1.0f, 1.0f);
    }

    // S, T (or X, Y)
    // Texture coordinate data.
    // Because images have a Y axis pointing downward (values increase as you move down the image) while
    // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
    // What's more is that the texture coordinates are the same for every face.
    public static float[] generateTextureData(float width, float height) {
        Point2D p1 = new Point2D(0.0f, 0.0f);
        Point2D p2 = new Point2D(width, 0.0f);
        Point2D p3 = new Point2D(0.0f, height);
        Point2D p4 = new Point2D(width, height);

        Point2DFace face = new Point2DFace(p1, p2, p3, p4);

        List<Face> faces = new ArrayList<>();
        for (int i = 0; i < FACES_PER_CUBE; i++) {
            faces.add(face);
        }

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
}
