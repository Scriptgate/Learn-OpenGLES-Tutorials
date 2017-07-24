package net.scriptgate.opengles.cube;


import net.scriptgate.opengles.program.AttributeVariable;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static net.scriptgate.nio.BufferHelper.allocateBuffer;
import static net.scriptgate.opengles.program.AttributeVariable.*;

public class CubeFactoryBuilder {

    private Map<AttributeVariable, FloatBuffer> cubeData;

    public static CubeFactoryBuilder createCubeFactory() {
        return new CubeFactoryBuilder();
    }

    private CubeFactoryBuilder() {
        cubeData = new HashMap<>();
    }

    public CubeFactoryBuilder positions(float[] positionData) {
        return addData(POSITION, positionData);
    }

    public CubeFactoryBuilder colors(float[] colorData) {
        return addData(COLOR, colorData);
    }

    public CubeFactoryBuilder normals(float[] normalData) {
        return addData(NORMAL, normalData);
    }

    public CubeFactoryBuilder textures(float[] textureData) {
        return addData(TEXTURE_COORDINATE, textureData);
    }

    private CubeFactoryBuilder addData(AttributeVariable type, float[] data) {
        cubeData.put(type, allocateBuffer(data));
        return this;
    }

    public CubeFactory build() {
        return new CubeFactory(cubeData);
    }

}
