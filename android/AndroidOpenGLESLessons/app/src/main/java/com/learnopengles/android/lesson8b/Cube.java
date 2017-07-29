package com.learnopengles.android.lesson8b;


import net.scriptgate.common.Point3D;

import java8.util.function.Function;
import java8.util.function.ToIntFunction;

class Cube {

    private final Point3D position;
    private final int colorIndex;

    Cube(Point3D position, int colorIndex) {
        this.position = position;
        this.colorIndex = colorIndex;
    }

    static ToIntFunction<Cube> toColorIndex() {
        return new ToIntFunction<Cube>() {
            @Override
            public int applyAsInt(Cube cube) {
                return cube.colorIndex;
            }
        };
    }

    static Function<Cube, Point3D> toPosition() {
        return new Function<Cube, Point3D>() {
            @Override
            public Point3D apply(Cube cube) {
                return cube.position;
            }
        };
    }

}
