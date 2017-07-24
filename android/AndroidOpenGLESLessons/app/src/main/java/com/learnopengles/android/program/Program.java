package com.learnopengles.android.program;

import android.content.Context;

import java.nio.FloatBuffer;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.BufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.program.AttributeVariable.sizeOf;
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

    public PassDataToAttribute pass(FloatBuffer data) {
        return new PassDataToAttribute(data);
    }

    public PassDataToAttribute pass(FloatBuffer data, AttributeVariable... structure) {
        return pass(data).withStructure(structure);
    }

    public BindBufferToAttribute bind(int bufferIndex) {
        return new BindBufferToAttribute(bufferIndex);
    }

    public BindBufferToAttribute bind(int bufferIndex, AttributeVariable... structure) {
        return bind(bufferIndex).withStructure(structure);
    }

    public class BindBufferToAttribute {

        private final int bufferIndex;
        private int stride = 0;
        private int position = 0;

        private BindBufferToAttribute(int bufferIndex) {
            this.bufferIndex = bufferIndex;
        }

        public BindBufferToAttribute withStructure(AttributeVariable... attributeVariables) {
            this.stride = sizeOf(attributeVariables) * BYTES_PER_FLOAT;
            return this;
        }

        public BindBufferToAttribute at(int position) {
            this.position = position;
            return this;
        }

        public BindBufferToAttribute after(AttributeVariable... attributeVariables) {
            return at(sizeOf(attributeVariables) * BYTES_PER_FLOAT);
        }

        public void to(AttributeVariable attributeVariable) {
            glBindBuffer(GL_ARRAY_BUFFER, bufferIndex);

            int handle = getHandle(attributeVariable);
            glVertexAttribPointer(handle, attributeVariable.getSize(), GL_FLOAT, false, stride, position);
            glEnableVertexAttribArray(handle);
        }
    }

    public class PassDataToAttribute {
        private final FloatBuffer data;
        private int stride = 0;

        private PassDataToAttribute(FloatBuffer data) {
            this.data = data;
        }

        public PassDataToAttribute at(int position) {
            data.position(position);
            return this;
        }

        public PassDataToAttribute after(AttributeVariable... attributeVariables) {
            return at(sizeOf(attributeVariables));
        }

        public PassDataToAttribute withStructure(AttributeVariable... attributeVariables) {
            this.stride = sizeOf(attributeVariables) * BYTES_PER_FLOAT;
            return this;
        }

        public void to(AttributeVariable attributeVariable) {
            int handle = getHandle(attributeVariable);
            glVertexAttribPointer(handle, attributeVariable.getSize(), GL_FLOAT, false, stride, data);
            glEnableVertexAttribArray(handle);
        }
    }
}
