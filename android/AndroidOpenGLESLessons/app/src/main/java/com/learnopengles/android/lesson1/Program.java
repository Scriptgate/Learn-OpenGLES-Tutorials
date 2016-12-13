package com.learnopengles.android.lesson1;

import com.learnopengles.android.lesson1.program.AttributeVariable;
import com.learnopengles.android.lesson1.program.UniformVariable;

import java.util.List;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.lesson1.Shader.loadShader;
import static com.learnopengles.android.lesson1.program.AttributeVariable.COLOR;
import static com.learnopengles.android.lesson1.program.AttributeVariable.POSITION;
import static com.learnopengles.android.lesson1.program.AttributeVariable.toStringArray;
import static java.util.Arrays.asList;

public class Program {

    private int handle;

    private Program(int programHandle) {
        this.handle = programHandle;
    }

    public static Program createProgram(String vertexShaderResource, String fragmentShaderResource) {

        int vertexShaderHandle = loadShader(GL_VERTEX_SHADER, vertexShaderResource);
        int fragmentShaderHandle = loadShader(GL_FRAGMENT_SHADER, fragmentShaderResource);

        return createProgram(vertexShaderHandle, fragmentShaderHandle, asList(POSITION, COLOR));
    }

    private static Program createProgram(int vertexShaderHandle, int fragmentShaderHandle, List<AttributeVariable> attributes) {
        // Create a program object and store the handle to it.
        int programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, toStringArray(attributes));
        return new Program(programHandle);
    }

    public int getHandle(AttributeVariable attribute) {
        return glGetAttribLocation(handle, attribute.getName());
    }

    public int getHandle(UniformVariable uniform) {
        return glGetUniformLocation(handle, uniform.getName());
    }

    public void useForRendering() {
        // Tell OpenGL to use this program when rendering.
        glUseProgram(handle);
    }
}
