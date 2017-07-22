package com.learnopengles.android.lesson1;

import com.learnopengles.android.common.ColorPoint3D;
import com.learnopengles.android.common.Point3D;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.lesson1.TriangleBuilder.generateData;
import static com.learnopengles.android.lesson1.TriangleBuilder.triangle;

public class TriangleBuilderTest {

    @Test
    public void addColorPoints() throws Exception {
        List<ColorPoint3D> points = new ArrayList<>();
        points.add(new ColorPoint3D(new Point3D(-0.5f, -0.25f, 0.0f), RED));
        points.add(new ColorPoint3D(new Point3D(0.5f, -0.25f, 0.0f), GREEN));
        points.add(new ColorPoint3D(new Point3D(0.0f, 0.559016994f, 0.0f), BLUE));
        float[] triangleVertices = generateData(points);

        float[] result = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        Assert.assertArrayEquals(triangleVertices, result, 0.000000001f);
    }

    @Test
    public void createEquilateralTriangle() throws Exception {
        float[] triangleVertices = generateData(TriangleBuilder.createEquilateralTriangle(1, RED, GREEN, BLUE));

        float[] result = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.28867513f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f,

                0.5f, -0.28867513f, 0.0f,
                0.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 0.57735026f, 0.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        Assert.assertArrayEquals(triangleVertices, result, 0.000000001f);

    }
}
