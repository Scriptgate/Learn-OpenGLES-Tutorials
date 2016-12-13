package com.learnopengles.android.lesson1;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.lesson1.Shader.loadShader;

public class Program {

    private int handle;

    private Program(int programHandle) {
        this.handle = programHandle;
    }

    public static Program createProgram(String vertexShaderResource, String fragmentShaderResource) {

        int vertexShaderHandle = loadShader(GL_VERTEX_SHADER, vertexShaderResource);
        int fragmentShaderHandle = loadShader(GL_FRAGMENT_SHADER, fragmentShaderResource);

        //TODO: remove magic strings
        // Create a program object and store the handle to it.
        int programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"a_Position", "a_Color"});
        return new Program(programHandle);
    }

    public int getPositionHandle() {
        return glGetAttribLocation(handle, "a_Position");
    }

    public int getColorHandle() {
        return glGetAttribLocation(handle, "a_Color");
    }

    public int getMVPHandle() {
        return glGetUniformLocation(handle, "u_MVPMatrix");
    }

    public void useForRendering() {
        // Tell OpenGL to use this program when rendering.
        glUseProgram(handle);
    }
}
