package com.learnopengles.android.program;

import android.content.Context;

import java.nio.FloatBuffer;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.program.Shader.loadShader;
import static com.learnopengles.android.program.AttributeVariable.toStringArray;

public class Program {

    private int handle;

    private Program(int programHandle) {
        this.handle = programHandle;
    }

    public static Program createProgram(Context context, int vertexShaderResource, int fragmentShaderResource, List<AttributeVariable> attributes) {
        int vertexShaderHandle = loadShader(GL_VERTEX_SHADER, context, vertexShaderResource);
        int fragmentShaderHandle = loadShader(GL_FRAGMENT_SHADER, context, fragmentShaderResource);

        int programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, toStringArray(attributes));
        return new Program(programHandle);
    }

    public static Program createProgram(String vertexShaderResource, String fragmentShaderResource, List<AttributeVariable> attributes) {
        int vertexShaderHandle = loadShader(GL_VERTEX_SHADER, vertexShaderResource);
        int fragmentShaderHandle = loadShader(GL_FRAGMENT_SHADER, fragmentShaderResource);

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

    public ProgramAttributeData pass(FloatBuffer data) {
        return new ProgramAttributeData(data);
    }

    @SuppressWarnings("WeakerAccess")// I don't know where Android Studio gets the idea that this class can be private.
    public class ProgramAttributeData {
        private final FloatBuffer data;

        private ProgramAttributeData(FloatBuffer data) {
            this.data = data;
        }

        public void to(AttributeVariable attributeVariable) {
            int handle = getHandle(attributeVariable);

            //FIXME: setting data to position 0 does not seem like the responsibility of Program. It also limits this method to be only used with FloatBuffers that contain only the data necessary
            data.position(0);

            glVertexAttribPointer(handle, attributeVariable.getSize(), GL_FLOAT, false, 0, data);
            glEnableVertexAttribArray(handle);
        }
    }
}
