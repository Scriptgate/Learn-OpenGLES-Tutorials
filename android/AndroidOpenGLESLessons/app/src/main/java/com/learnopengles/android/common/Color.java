package com.learnopengles.android.common;

public class Color {

    //@formatter:off
    public static final Color RED     = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Color GREEN   = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Color BLUE    = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    public static final Color WHITE   = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Color GREY    = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    public static final Color BLACK   = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color MAGENTA = new Color(1.0f, 0.0f, 1.0f, 1.0f);
    public static final Color YELLOW  = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    public static final Color CYAN    = new Color(0.0f, 1.0f, 1.0f, 1.0f);
    //@formatter:on

    public final float red;
    public final float green;
    public final float blue;
    public final float alpha;


    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
}
