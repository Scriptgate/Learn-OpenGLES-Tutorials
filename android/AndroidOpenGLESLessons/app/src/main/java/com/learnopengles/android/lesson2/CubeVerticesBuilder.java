package com.learnopengles.android.lesson2;

import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.Color;

import java.util.ArrayList;
import java.util.List;

public class CubeVerticesBuilder {

    public static CubeVerticesBuilder vertices() {
        return new CubeVerticesBuilder();
    }

    float width = 1f;
    float height = 1f;
    private Point frontA = new Point(-width, height, 1.0f);
    private Point frontB = new Point(-width, -height, 1.0f);
    private Point frontC = new Point(width, height, 1.0f);
    private Point frontD = new Point(width, -height, 1.0f);

    private Point backE = new Point(width, height, -1.0f);
    private Point backF = new Point(width, -height, -1.0f);
    private Point backG = new Point(-width, height, -1.0f);
    private Point backH = new Point(-width, -height, -1.0f);

    private CubeVerticesBuilder() {

    }

    public float[] color() {
        List<Color> colors = new ArrayList<>();
        // R, G, B, frontA

        // Front face
        colors.add(new Color(1.0f, 0.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 0.0f, 1.0f));

        // Right face
        colors.add(new Color(0.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 0.0f, 1.0f));

        // Back face
        colors.add(new Color(0.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 0.0f, 1.0f, 1.0f));

        // Left face
        colors.add(new Color(1.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 0.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 0.0f, 1.0f));

        // Top face
        colors.add(new Color(0.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(0.0f, 1.0f, 1.0f, 1.0f));

        // Bottom face
        colors.add(new Color(1.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 0.0f, 1.0f, 1.0f));


        float[] result = new float[colors.size() * 4];
        for (int i = 0; i < colors.size(); i++) {
            Color point = colors.get(i);
            result[i * 4] = point.red;
            result[i * 4 + 1] = point.green;
            result[i * 4 + 2] = point.blue;
            result[i * 4 + 3] = point.alpha;
        }
        return result;
    }
    public float[] white() {
        List<Color> colors = new ArrayList<>();
        // R, G, B, frontA

        // Front face
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));

        // Right face
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));

        // Back face
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));

        // Left face
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));

        // Top face
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));

        // Bottom face
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Color(1.0f, 1.0f, 1.0f, 1.0f));


        float[] result = new float[colors.size() * 4];
        for (int i = 0; i < colors.size(); i++) {
            Color point = colors.get(i);
            result[i * 4] = point.red;
            result[i * 4 + 1] = point.green;
            result[i * 4 + 2] = point.blue;
            result[i * 4 + 3] = point.alpha;
        }
        return result;
    }

    public float[] position() {
        List<Point> points = new ArrayList<>();

        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
        // usually represent the backside of an object and aren't visible anyways.

        // Front face
        points.add(frontA);
        points.add(frontB);
        points.add(frontC);
        points.add(frontB);
        points.add(frontD);
        points.add(frontC);

        // Right face
        points.add(frontC);
        points.add(frontD);
        points.add(backE);
        points.add(frontD);
        points.add(backF);
        points.add(backE);

        // Back face
        points.add(backE);
        points.add(backF);
        points.add(backG);
        points.add(backF);
        points.add(backH);
        points.add(backG);

        // Left face
        points.add(backG);
        points.add(backH);
        points.add(frontA);
        points.add(backH);
        points.add(frontB);
        points.add(frontA);

        // Top face
        points.add(backG);
        points.add(frontA);
        points.add(backE);
        points.add(frontA);
        points.add(frontC);
        points.add(backE);

        // Bottom face
        points.add(backF);
        points.add(frontD);
        points.add(backH);
        points.add(frontD);
        points.add(frontB);
        points.add(backH);


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
