package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Point2D;
import com.learnopengles.android.common.Point3D;

class Vertex {

    Point3D position;
    Point2D textureCoordinate;

    Vertex(Point3D position, Point2D textureCoordinate) {
        this.position = position;
        this.textureCoordinate = textureCoordinate;
    }
}