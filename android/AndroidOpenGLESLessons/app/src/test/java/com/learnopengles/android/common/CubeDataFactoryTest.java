package com.learnopengles.android.common;

import com.learnopengles.android.cube.CubeDataFactory;

import org.junit.Test;

import static com.learnopengles.android.common.Color.BLUE;
import static com.learnopengles.android.common.Color.CYAN;
import static com.learnopengles.android.common.Color.GREEN;
import static com.learnopengles.android.common.Color.MAGENTA;
import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.Color.YELLOW;
import static com.learnopengles.android.cube.CubeDataFactory.generateColorData;
import static com.learnopengles.android.cube.CubeDataFactory.generatePositionData;
import static org.junit.Assert.assertArrayEquals;

public class CubeDataFactoryTest {

    public static final float DELTA = 0.000000001f;

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
    final float[] COLOR = {
            // Front face (red)
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            // Right face (green)
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,

            // Back face (blue)
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            // Left face (yellow)
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,

            // Top face (cyan)
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,

            // Bottom face (magenta)
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f
    };
    //@formatter:on


    // X, Y, Z
    // The normal is used in light calculations and is a vector which points
    // orthogonal to the plane of the surface. For a cube model, the normals
    // should be orthogonal to the points of each face.
    //@formatter:off
    final float[] NORMAL =
            {
            // Front face
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            // Right face
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            // Back face
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,

            // Left face
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,

            // Top face
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            // Bottom face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f
    };
    //@formatter:on

    // S, T (or X, Y)
    // Texture coordinate data.
    // Because images have a Y axis pointing downward (values increase as you move down the image) while
    // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
    // What's more is that the texture coordinates are the same for every face.
    //@formatter:off
    final float[] TEXTURE = {
            // Front face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Right face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Back face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Left face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Top face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Bottom face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };
    //@formatter:on

    @Test
    public void generatePositionData_givenPoints() throws Exception {
        //@formatter:off
        final Point3D frontA = new Point3D(-1.0f,  1.0f,  1.0f);
        final Point3D frontB = new Point3D( 1.0f,  1.0f,  1.0f);
        final Point3D frontC = new Point3D(-1.0f, -1.0f,  1.0f);
        final Point3D frontD = new Point3D( 1.0f, -1.0f,  1.0f);
        final Point3D backA  = new Point3D(-1.0f,  1.0f, -1.0f);
        final Point3D backB  = new Point3D( 1.0f,  1.0f, -1.0f);
        final Point3D backC  = new Point3D(-1.0f, -1.0f, -1.0f);
        final Point3D backD  = new Point3D( 1.0f, -1.0f, -1.0f);
        //@formatter:on

        float[] positionData = generatePositionData(frontA, frontB, frontC, frontD, backA, backB, backC, backD);

        assertArrayEquals(positionData, POSITION, DELTA);
    }

    @Test
    public void generatePositionData_givenDimensions() throws Exception {
        float width = 1;
        float height = 1;
        float depth = 1;

        float[] positionData = generatePositionData(width, height, depth);

        assertArrayEquals(positionData, POSITION, DELTA);
    }

    @Test
    public void generateColorData_perFace() throws Exception {
        float[] colorData = generateColorData(RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA);

        assertArrayEquals(colorData, COLOR, DELTA);
    }

    @Test
    public void generateNormalData() throws Exception {
        float[] normalData = CubeDataFactory.generateNormalData();

        assertArrayEquals(normalData, NORMAL, DELTA);
    }

    @Test
    public void generateNormalData_perFace() throws Exception {
        Point3D front = new Point3D(0.0f, 0.0f, 1.0f);
        Point3D right = new Point3D(1.0f, 0.0f, 0.0f);
        Point3D back = new Point3D(0.0f, 0.0f, -1.0f);
        Point3D left = new Point3D(-1.0f, 0.0f, 0.0f);
        Point3D top = new Point3D(0.0f, 1.0f, 0.0f);
        Point3D bottom = new Point3D(0.0f, -1.0f, 0.0f);

        float[] normalData = CubeDataFactory.generateNormalData(front, right, back, left, top, bottom);

        assertArrayEquals(normalData, NORMAL, DELTA);
    }

    @Test
    public void generateTextureData() throws Exception {
        float[] textureData = CubeDataFactory.generateTextureData();

        assertArrayEquals(textureData, TEXTURE, DELTA);
    }
}