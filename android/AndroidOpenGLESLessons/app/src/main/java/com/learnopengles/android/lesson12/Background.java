package com.learnopengles.android.lesson12;


import android.os.SystemClock;

import net.scriptgate.common.Point3D;
import net.scriptgate.opengles.face.Point3DFace;

import java.util.ArrayList;
import java.util.List;

import java8.util.function.Consumer;

import static com.learnopengles.android.lesson12.Square.ELEMENTS_PER_FACE;
import static com.learnopengles.android.lesson12.Square.createSquare;
import static com.learnopengles.android.lesson12.SquareDataFactory.generateTextureData;
import static java8.util.stream.StreamSupport.stream;

class Background {

    private static final float SCALE = 50;

    private List<Square> background;

    Background() {
        background = new ArrayList<>();

        Point3DFace face = new Point3DFace(
                new Point3D(0, 0, 0),
                new Point3D(1.0f, 0, 0),
                new Point3D(0, 1.0f, 0),
                new Point3D(1.0f, 1.0f, 0)
        );
        float[] verticesData = new float[ELEMENTS_PER_FACE * face.getNumberOfElements()];
        face.addFaceToArray(verticesData, 0);
        float[] textureData = generateTextureData(5.0f, 5.0f);

        background.add(createSquare(new Point3D(-SCALE / 2, 0, -7.5f), new Point3D(), verticesData, textureData));
        background.add(createSquare(new Point3D(-SCALE / 2, -SCALE, -7.5f), new Point3D(), verticesData, textureData));
        background.add(createSquare(new Point3D(-SCALE / 2, -2 * SCALE, -7.5f), new Point3D(), verticesData, textureData));

        stream(background).forEach(new Consumer<Square>() {
            @Override
            public void accept(Square square) {
                square.setScale(new Point3D(SCALE, SCALE, 1));
            }
        });
    }

    public void setTexture(final int texture) {
        stream(background).forEach(new Consumer<Square>() {
            @Override
            public void accept(Square square) {
                square.setTexture(texture);
            }
        });
    }

    public void render(Consumer<Square> draw) {
        stream(background).forEach(draw);
    }

    void update() {
        long time = SystemClock.uptimeMillis() % 10_000L;
        final float distance = (0.03f / 10_000.0f) * ((int) time);

        stream(background).forEach(new Consumer<Square>() {
            @Override
            public void accept(Square square) {
                if (square.getPosition().y() > SCALE) {
                    square.translate(new Point3D(0, -SCALE * background.size(), 0));
                }
                square.translate(new Point3D(0, distance, 0));
            }
        });
    }
}
