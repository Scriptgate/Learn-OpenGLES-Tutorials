package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Point2D;

import java8.util.function.IntFunction;

class TextureTriangle {

    Point2D p1;
    Point2D p2;
    Point2D p3;

    TextureTriangle(int colorIndex) {
        float offsetX = 0.25f * (colorIndex % 4);
        float offsetY = 0.25f * (colorIndex / 4);

        //@formatter:off
        this.p1 = new Point2D(offsetX,         offsetY);
        this.p2 = new Point2D(offsetX + 0.25f, offsetY);
        this.p3 = new Point2D(offsetX,         offsetY + 0.25f);
        //@formatter:on
    }

    static IntFunction<TextureTriangle> toTextureTriangle() {
        return new IntFunction<TextureTriangle>() {
            @Override
            public TextureTriangle apply(int colorIndex) {
                return new TextureTriangle(colorIndex);
            }
        };
    }
}
