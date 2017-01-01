package com.learnopengles.android.cube.data.type;

public class TextureCubeData extends CubeDataBase {

    private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;

    public TextureCubeData(float[] textureCoordinateData) {
        super(textureCoordinateData, TEXTURE_COORDINATE_DATA_SIZE);
    }
}
