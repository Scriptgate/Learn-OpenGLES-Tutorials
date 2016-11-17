package com.learnopengles.android.lesson2;

import org.junit.Assert;
import org.junit.Test;

import static com.learnopengles.android.lesson2.CubeVerticesBuilder.vertices;

public class CubeVerticesBuilderTest {

    @Test
    public void position() throws Exception {
        // X, Y, Z
        final float[] result =
                {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        //@formatter:off

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
                        //@formatter:on
                };

        float[] cubeVertices = vertices().position().build();

        Assert.assertArrayEquals(cubeVertices, result, 0.000000001f);

    }
}