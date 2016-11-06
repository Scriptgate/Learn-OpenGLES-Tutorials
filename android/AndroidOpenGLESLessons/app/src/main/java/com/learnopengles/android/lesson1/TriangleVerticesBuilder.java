package com.learnopengles.android.lesson1;


import com.learnopengles.android.common.Point;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

public class TriangleVerticesBuilder {

    List<ColorPoint> points = new ArrayList<>(3);

    public static TriangleVerticesBuilder vertices() {
        return new TriangleVerticesBuilder();
    }

    public TriangleVerticesBuilder addColorPoint(Point point, Color color) {
        if (points.size() >= 3) {
            throw new IllegalArgumentException("A triangle has a maximum of 3 points");
        }
        points.add(new ColorPoint(point, color));
        return this;
    }

    private TriangleVerticesBuilder() {
    }

    public float[] build() {
        if (points.size() != 3) {
            throw new IllegalArgumentException("A triangle requires 3 points");
        }
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

    public TriangleVerticesBuilder createEquilateralTriangle(float length, Color point1, Color point2, Color point3) {
        float height = (float) sin(toRadians(60)) * length;
        float center = (float) tan(toRadians(30)) * length / 2;
        return vertices()
                .addColorPoint(new Point(-length / 2, -center, 0.0f), point1)
                .addColorPoint(new Point(length / 2, -center, 0.0f), point2)
                .addColorPoint(new Point(0.0f, height - center, 0.0f), point3);
    }
}
