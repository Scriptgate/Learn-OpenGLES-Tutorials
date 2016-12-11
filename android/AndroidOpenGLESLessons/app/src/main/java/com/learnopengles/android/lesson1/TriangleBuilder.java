package com.learnopengles.android.lesson1;


import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point3D;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

public class TriangleBuilder {

    List<ColorPoint> points;
    private Point3D position;
    private Integer rotationY;
    private Integer rotationX;

    public static TriangleBuilder triangle() {
        return new TriangleBuilder();
    }

    private TriangleBuilder() {
    }

    public Triangle build() {
        float[] vertices = generateData(points);
        Triangle triangle = new Triangle(vertices);
        if(position != null) {
            triangle.setPosition(position);
        }
        if(rotationX != null) {
            triangle.setRotationX(rotationX);
        }
        if(rotationY != null) {
            triangle.setRotationY(rotationY);
        }
        return triangle;
    }

    public static float[] generateData(List<ColorPoint> points) {
        float[] vertices = new float[21];
        for (int i = 0; i < points.size(); i++) {
            vertices[i * 7] = points.get(i).point.x;
            vertices[i * 7 + 1] = points.get(i).point.y;
            vertices[i * 7 + 2] = points.get(i).point.z;
            vertices[i * 7 + 3] = points.get(i).color.red;
            vertices[i * 7 + 4] = points.get(i).color.green;
            vertices[i * 7 + 5] = points.get(i).color.blue;
            vertices[i * 7 + 6] = points.get(i).color.alpha;
        }
        return vertices;
    }

    public TriangleBuilder equilateral(float length, Color pointA, Color pointB, Color pointC) {
        this.points = createEquilateralTriangle(length, pointA, pointB, pointC);
        return this;
    }

    public static List<ColorPoint> createEquilateralTriangle(float length, Color pointA, Color pointB, Color pointC) {
        float height = (float) sin(toRadians(60)) * length;
        float center = (float) tan(toRadians(30)) * length / 2;
        List<ColorPoint> points = new ArrayList<>();
        points.add(new ColorPoint(new Point3D(-length / 2, -center, 0.0f), pointA));
        points.add(new ColorPoint(new Point3D(length / 2, -center, 0.0f), pointB));
        points.add(new ColorPoint(new Point3D(0.0f, height - center, 0.0f), pointC));
        return points;
    }

    public TriangleBuilder position(Point3D position) {
        this.position = position;
        return this;
    }

    public TriangleBuilder rotateY(int angleInDegrees) {
        rotationY = angleInDegrees;
        return this;
    }

    public TriangleBuilder rotateX(int angleInDegrees) {
        rotationX = angleInDegrees;
        return this;
    }
}
