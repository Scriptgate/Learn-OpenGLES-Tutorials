package com.learnopengles.android.program;

import static com.learnopengles.android.common.RawResourceReader.readShaderFileFromResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;

class Shader {

    static int loadShader(int shaderType, String resource) {
        final String shaderSource = readShaderFileFromResource(resource);
        return compileShader(shaderType, shaderSource);
    }
}
