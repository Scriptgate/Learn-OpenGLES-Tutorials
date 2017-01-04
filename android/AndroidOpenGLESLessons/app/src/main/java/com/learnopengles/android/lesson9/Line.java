package com.learnopengles.android.lesson9;

import com.learnopengles.android.common.Color;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.drawable.Drawable;
import com.learnopengles.android.renderer.drawable.DrawableColorRenderer;
import com.learnopengles.android.renderer.DrawArraysRenderer;
import com.learnopengles.android.renderer.MVPRenderer;
import com.learnopengles.android.renderer.drawable.DrawablePositionRenderer;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_LINES;
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