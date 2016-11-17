package com.learnopengles.android.lesson2;

import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.PointBuilder;

import java.util.ArrayList;
import java.util.List;

public class CubeVerticesBuilder {

    public static CubeVerticesBuilder vertices() {
        return new CubeVerticesBuilder();
    }

    private CubeVerticesBuilder() {

    }

    public CubeVerticesBuilder position() {
        return this;
    }

    public float[] build() {
        List<Point> points = new ArrayList<>();

        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
        // usually represent the backside of an object and aren't visible anyways.
        Point A = new Point(1.0f, 1.0f, 0.0f);
        Point B = new Point(1.0f, -1.0f, 0.0f);
        Point C = new Point(-1.0f, -1.0f, 0.0f);
        Point D = new Point(-1.0f, 1.0f, 0.0f);

        PointBuilder frontFace = new PointBuilder(new Point(0.0f, 0.0f, 1.0f));
        //        points.add(A);
        points.add(frontFace.xy(D.x, D.y));
        points.add(frontFace.xy(C.x, C.y));
        points.add(frontFace.xy(A.x, A.y));
        points.add(frontFace.xy(C.x, C.y));
        points.add(frontFace.xy(B.x, B.y));
        points.add(frontFace.xy(A.x, A.y));

        PointBuilder rightFace = new PointBuilder(new Point(1.0f, 0.0f, 0.0f));

        points.add(rightFace.yz(A.x, A.y));
        points.add(rightFace.yz(D.x, D.y));
        points.add(rightFace.yz(B.x, B.y));
        points.add(rightFace.yz(D.x, D.y));
        points.add(rightFace.yz(C.x, C.y));
        points.add(rightFace.yz(B.x, B.y));

        PointBuilder backFace = new PointBuilder(new Point(0.0f, 0.0f, -1.0f));
        points.add(backFace.xy(A.x, A.y));
        points.add(backFace.xy(B.x, B.y));
        points.add(backFace.xy(D.x, D.y));
        points.add(backFace.xy(B.x, B.y));
        points.add(backFace.xy(C.x, C.y));
        points.add(backFace.xy(D.x, D.y));

        PointBuilder leftFace = new PointBuilder(new Point(-1.0f, 0.0f, 0.0f));
        points.add(leftFace.yz(B.x, B.y));
        points.add(leftFace.yz(C.x, C.y));
        points.add(leftFace.yz(A.x, A.y));
        points.add(leftFace.yz(C.x, C.y));
        points.add(leftFace.yz(D.x, D.y));
        points.add(leftFace.yz(A.x, A.y));

        PointBuilder topFace = new PointBuilder(new Point(0.0f, 1.0f, 0.0f));
        points.add(topFace.xz(C.x, C.y));
        points.add(topFace.xz(D.x, D.y));
        points.add(topFace.xz(B.x, B.y));
        points.add(topFace.xz(D.x, D.y));
        points.add(topFace.xz(A.x, A.y));
        points.add(topFace.xz(B.x, B.y));

        PointBuilder bottomFace = new PointBuilder(new Point(0.0f, -1.0f, 0.0f));
        points.add(bottomFace.xz(B.x, B.y));
        points.add(bottomFace.xz(A.x, A.y));
        points.add(bottomFace.xz(C.x, C.y));
        points.add(bottomFace.xz(A.x, A.y));
        points.add(bottomFace.xz(D.x, D.y));
        points.add(bottomFace.xz(C.x, C.y));

        float[] result = new float[points.size() * 3];
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            result[i * 3] = point.x;
            result[i * 3 + 1] = point.y;
            result[i * 3 + 2] = point.z;
        }
        return result;
    }

}
