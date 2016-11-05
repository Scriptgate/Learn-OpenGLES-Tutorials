package com.learnopengles.android.lesson1;

import org.junit.Assert;
import org.junit.Test;

import static com.learnopengles.android.lesson1.Color.BLUE;
import static com.learnopengles.android.lesson1.Color.GREEN;
import static com.learnopengles.android.lesson1.Color.RED;
import static com.learnopengles.android.lesson1.TriangleVerticesBuilder.vertices;

public class TriangleVerticesBuilderTest {

    @Test
    public void addColorPoints() throws Exception {
        float[] triangleVertices = vertices()
                .addColorPoint(new Point(-0.5f, -0.25f, 0.0f), RED)
                .addColorPoint(new Point(0.5f, -0.25f, 0.0f), GREEN)
                .addColorPoint(new Point(0.0f, 0.559016994f, 0.0f), BLUE)
                .build();

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
        float[] triangleVertices = vertices()
                .createEquilateralTriangle(1, RED, GREEN, BLUE)
                .build();

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
