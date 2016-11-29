package com.learnopengles.android.lesson2;

import com.learnopengles.android.common.Color;

import java.util.ArrayList;
import java.util.List;

public class CubeVerticesBuilder {

    public static CubeVerticesBuilder vertices() {
        return new CubeVerticesBuilder();
    }

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

}
