package com.learnopengles.android.common;

import org.junit.Assert;
import org.junit.Test;

import static com.learnopengles.android.lesson2.CubeVerticesBuilder.vertices;
import static org.junit.Assert.*;

public class ShapeBuilderTest {

    // X, Y, Z
    // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
    // if the points are counter-clockwise we are looking at the "front". If not we are looking at
    // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
    // usually represent the backside of an object and aren't visible anyways.
    //@formatter:off
    private static final float[] POSITION = {
            // Front face
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
             1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
             1.0f, -1.0f,  1.0f,
             1.0f,  1.0f,  1.0f,

            // Right face
             1.0f,  1.0f,  1.0f,
             1.0f, -1.0f,  1.0f,
             1.0f,  1.0f, -1.0f,
             1.0f, -1.0f,  1.0f,
             1.0f, -1.0f, -1.0f,
             1.0f,  1.0f, -1.0f,

            // Back face
             1.0f,  1.0f, -1.0f,
             1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
             1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,

            // Left face
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,

            // Top face
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
             1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
             1.0f,  1.0f,  1.0f,
             1.0f,  1.0f, -1.0f,

            // Bottom face
             1.0f, -1.0f, -1.0f,
             1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
             1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
    };
    //@formatter:on

    // R, G, B, A
    //@formatter:off
    private static final float[] COLOR = {

    };
    //@formatter:on

    @Test
    public void generateCubeData() throws Exception {
        //@formatter:off
        final Point frontA = new Point(-1.0f,  1.0f,  1.0f);
        final Point frontB = new Point( 1.0f,  1.0f,  1.0f);
        final Point frontC = new Point(-1.0f, -1.0f,  1.0f);
        final Point frontD = new Point( 1.0f, -1.0f,  1.0f);
        final Point backA  = new Point(-1.0f,  1.0f, -1.0f);
        final Point backB  = new Point( 1.0f,  1.0f, -1.0f);
        final Point backC  = new Point(-1.0f, -1.0f, -1.0f);
        final Point backD  = new Point( 1.0f, -1.0f, -1.0f);
        //@formatter:on

        float[] cubeVertices = ShapeBuilder.generateCubeData(frontA, frontB, frontC, frontD, backA, backB, backC, backD);

        Assert.assertArrayEquals(cubeVertices, POSITION, 0.000000001f);
    }

    @Test
    public void generateCubeData_fromDimensions() throws Exception {
        float width = 1;
        float height = 1;
        float depth = 1;

        float[] cubeVertices = ShapeBuilder.generateCubeData(width, height, depth);

        Assert.assertArrayEquals(cubeVertices, POSITION, 0.000000001f);
    }
}