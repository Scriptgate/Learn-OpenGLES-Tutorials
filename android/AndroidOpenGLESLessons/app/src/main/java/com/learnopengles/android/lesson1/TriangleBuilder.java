package com.learnopengles.android.lesson1;


import net.scriptgate.android.common.Color;
import net.scriptgate.android.common.ColorPoint3D;
import net.scriptgate.android.common.Point3D;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

class TriangleBuilder {

    private List<ColorPoint3D> points;
    private Point3D position;
    private Integer rotationY;
    private Integer rotationX;

    static TriangleBuilder triangle() {
        return new TriangleBuilder();
    }

    private TriangleBuilder() {
    }

    Triangle build() {
        float[] vertices = generateData(points);
        Triangle triangle = new Triangle(vertices);
        if (position != null) {
            triangle.setPosition(position);
        }
        if (rotationX != null) {
            triangle.setRotationX(rotationX);
        }
        if (rotationY != null) {
            triangle.setRotationY(rotationY);
        }
        return triangle;
    }

    static float[] generateData(List<ColorPoint3D> points) {
        float[] vertices = new float[21];
        for (int i = 0; i < points.size(); i++) {
            vertices[i * 7] = points.get(i).x();
            vertices[i * 7 + 1] = points.get(i).y();
            vertices[i * 7 + 2] = points.get(i).z();
            vertices[i * 7 + 3] = points.get(i).red();
            vertices[i * 7 + 4] = points.get(i).green();
            vertices[i * 7 + 5] = points.get(i).blue();
            vertices[i * 7 + 6] = points.get(i).alpha();
        }
        return vertices;
    }

    TriangleBuilder equilateral(float length, Color pointA, Color pointB, Color pointC) {
        this.points = createEquilateralTriangle(length, pointA, pointB, pointC);
        return this;
    }

    static List<ColorPoint3D> createEquilateralTriangle(float length, Color pointA, Color pointB, Color pointC) {
        float height = (float) sin(toRadians(60)) * length;
        float center = (float) tan(toRadians(30)) * length / 2;
        List<ColorPoint3D> points = new ArrayList<>();
        points.add(new ColorPoint3D(new Point3D(-length / 2, -center, 0.0f), pointA));
        points.add(new ColorPoint3D(new Point3D(length / 2, -center, 0.0f), pointB));
        points.add(new ColorPoint3D(new Point3D(0.0f, height - center, 0.0f), pointC));
        return points;
    }

    TriangleBuilder position(float x, float y, float z) {
        this.position = new Point3D(x, y, z);
        return this;
    }

    TriangleBuilder rotateY(int angleInDegrees) {
        rotationY = angleInDegrees;
        return this;
    }

    TriangleBuilder rotateX(int angleInDegrees) {
        rotationX = angleInDegrees;
        return this;
    }
}
