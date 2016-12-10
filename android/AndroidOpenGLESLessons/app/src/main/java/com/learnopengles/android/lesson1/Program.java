package com.learnopengles.android.lesson1;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.lesson1.Shader.loadShader;

public class Program {

    public static int createProgram(String vertexShaderResource, String fragmentShaderResource) {

        int vertexShaderHandle = loadShader(GL_VERTEX_SHADER, vertexShaderResource);
        int fragmentShaderHandle = loadShader(GL_FRAGMENT_SHADER, fragmentShaderResource);

        //TODO: remove magic strings
        return createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {"a_Position","a_Color"});
    }

}
