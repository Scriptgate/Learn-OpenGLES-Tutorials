package com.learnopengles.android.lesson12;

import net.scriptgate.android.common.Point2D;
import net.scriptgate.android.opengles.face.Point2DFace;

import static com.learnopengles.android.lesson12.Square.ELEMENTS_PER_FACE;

public class SquareDataFactory {

    // S, T (or X, Y)
    // Texture coordinate data.
    // Because images have a Y axis pointing downward (values increase as you move down the image) while
    // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
    // What's more is that the texture coordinates are the same for every face.
    public static float[] generateTextureData(float width, float height) {
        Point2DFace face = new Point2DFace(
                new Point2D(0.0f, 0.0f),
                new Point2D(width, 0.0f),
                new Point2D(0.0f, height),
                new Point2D(width, height)
        );
        float[] textureData = new float[ELEMENTS_PER_FACE * face.getNumberOfElements()];
        face.addFaceToArray(textureData, 0);
        return textureData;
    }

    // S, T (or X, Y)
    // Texture coordinate data.
    // Because images have a Y axis pointing downward (values increase as you move down the image) while
    // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
    // What's more is that the texture coordinates are the same for every face.
    public static float[] generateTextureData(float width, float height, Point2D offset) {
        Point2DFace face = new Point2DFace(
                new Point2D(offset.x(), offset.y()),
                new Point2D(offset.x() + width, offset.y()),
                new Point2D(offset.x(), offset.y() + height),
                new Point2D(offset.x() + width, offset.y() + height)
        );
        float[] textureData = new float[ELEMENTS_PER_FACE * face.getNumberOfElements()];
        face.addFaceToArray(textureData, 0);
        return textureData;
    }
}
