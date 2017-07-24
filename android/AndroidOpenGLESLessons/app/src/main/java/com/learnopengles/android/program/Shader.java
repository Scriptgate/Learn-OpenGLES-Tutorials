package com.learnopengles.android.program;

import android.content.Context;

import static com.learnopengles.android.common.RawResourceReader.readShaderFileFromResource;
import static com.learnopengles.android.common.RawResourceReader.readTextFileFromRawResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;

class Shader {

    static int loadShader(int shaderType, Context context, int resourceId) {
        String shaderSource = readTextFileFromRawResource(context, resourceId);
        return  compileShader(shaderType, shaderSource);
    }

    static int loadShader(int shaderType, String resource) {
        final String shaderSource = readShaderFileFromResource(resource);
        return compileShader(shaderType, shaderSource);
    }
}
