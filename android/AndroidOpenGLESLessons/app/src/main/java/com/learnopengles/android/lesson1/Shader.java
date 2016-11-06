package com.learnopengles.android.lesson1;

import static com.learnopengles.android.common.RawResourceReader.readShaderFileFromResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;

public class Shader {

    public static int loadShader(int shaderType, String resource) {
        final String shaderSource = readShaderFileFromResource(resource);
        return compileShader(shaderType, shaderSource);
    }
}
