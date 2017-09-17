package com.learnopengles.android.lesson11;

import net.scriptgate.android.common.Color;
import net.scriptgate.android.common.Point3D;

import java.nio.FloatBuffer;

import static net.scriptgate.android.common.Color.RED;
import static net.scriptgate.android.nio.BufferHelper.allocateBuffer;

class Line implements Drawable {

    private FloatBuffer vertexBuffer;

    private Color color = RED;

    Line(Color color, Point3D from, Point3D to) {
        this.color = color;
        setPoints(from, to);
    }

    private void setPoints(Point3D from, Point3D to) {
        vertexBuffer = allocateBuffer(new float[]{from.x(), from.y(), from.z(), to.x(), to.y(), to.z()});
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public float[] getColor() {
        return color.toFloatArray();
    }

    @Override
    public FloatBuffer getPositionData() {
        return vertexBuffer;
    }
}