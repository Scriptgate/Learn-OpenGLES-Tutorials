package com.learnopengles.android.renderer.drawable;


import java.nio.FloatBuffer;

public interface Drawable {

    float[] getColor();

    FloatBuffer getPositionData();
}
