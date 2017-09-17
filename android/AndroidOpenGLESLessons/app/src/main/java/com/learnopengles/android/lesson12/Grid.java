package com.learnopengles.android.lesson12;


import android.os.SystemClock;

import net.scriptgate.android.common.Point3D;
import net.scriptgate.android.opengles.face.Point3DFace;

import java.util.ArrayList;
import java.util.List;

import java8.util.function.Consumer;

import static com.learnopengles.android.lesson12.Square.ELEMENTS_PER_FACE;
import static com.learnopengles.android.lesson12.Square.createSquare;
import static com.learnopengles.android.lesson12.SquareDataFactory.generateTextureData;
import static java8.util.stream.StreamSupport.stream;

class Grid {

    private static final float SCALE = 25;

    private List<Square> grid;

    Grid() {
        grid = new ArrayList<>();

        Point3DFace face = new Point3DFace(
                new Point3D(-2, 0, -1),
                new Point3D(2, 0, -1),
                new Point3D(-2, 0, 1),
                new Point3D(2, 0, 1)
        );
        float[] verticesData = new float[ELEMENTS_PER_FACE * face.getNumberOfElements()];
        face.addFaceToArray(verticesData, 0);

        grid.add(createSquare(new Point3D(), new Point3D(), verticesData, generateTextureData(SCALE, SCALE/2)));
        grid.add(createSquare(new Point3D(0,0,-SCALE), new Point3D(), verticesData, generateTextureData(SCALE, SCALE/2)));
        grid.add(createSquare(new Point3D(0,0,-SCALE*2), new Point3D(), verticesData, generateTextureData(SCALE, SCALE/2)));


        for (Square square : grid) {
            square.setScale(new Point3D(SCALE, 1.0f, SCALE/2));
        }

    }

    public void setTexture(final int texture) {
        stream(grid).forEach(new Consumer<Square>() {
            @Override
            public void accept(Square square) {
                square.setTexture(texture);
            }
        });
    }

    public void render(Consumer<Square> draw) {
        stream(grid).forEach(draw);
    }


    void update() {
        long time = SystemClock.uptimeMillis() % 10_000L;
        final float distance = (0.3f / 10_000.0f) * ((int) time);

        stream(grid).forEach(new Consumer<Square>() {
            @Override
            public void accept(Square square) {
                if(square.getPosition().z() > SCALE) {
                    square.translate(new Point3D(0,0,-SCALE*grid.size()));
                }
                square.translate(new Point3D(0,0,distance));
            }
        });

    }
}
