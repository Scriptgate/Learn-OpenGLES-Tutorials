package com.learnopengles.android.lesson11;

import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.renderer.drawable.Drawable;

import java.nio.FloatBuffer;

import static com.learnopengles.android.common.Color.RED;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;

public class Line implements Drawable {

    private FloatBuffer vertexBuffer;

    private Color color = RED;

    public Line(Color color, Point3D from, Point3D to) {
        this.color = color;
        setPoints(from, to);
    }

    private void setPoints(Point3D from, Point3D to) {
        vertexBuffer = allocateBuffer(new float[]{from.x, from.y, from.z, to.x, to.y, to.z});
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public float[] getColor() {
        return color.toArray();
    }

    @Override
    public FloatBuffer getPositionData() {
        return vertexBuffer;
    }
}