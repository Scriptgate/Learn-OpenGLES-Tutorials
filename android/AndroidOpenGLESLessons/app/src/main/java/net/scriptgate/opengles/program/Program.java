package net.scriptgate.opengles.program;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.List;

import static android.opengl.GLES20.*;
import static java.util.Arrays.asList;
import static net.scriptgate.nio.BufferHelper.BYTES_PER_FLOAT;
import static net.scriptgate.opengles.program.AttributeVariable.sizeOf;
import static net.scriptgate.opengles.program.UniformVariable.TEXTURE;

public class Program {

    private int handle;

    Program(int programHandle) {
        this.handle = programHandle;
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

    public void bindTexture(int texture) {
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, texture);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(getHandle(TEXTURE), 0);
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

        public PassDataToAttribute after(Collection<AttributeVariable> attributeVariables) {
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

        public void structuredAs(AttributeVariable... structure) {
            List<AttributeVariable> structureList = asList(structure);

            for (AttributeVariable attributeToPassTo : structureList) {
                int positionInStructure = structureList.indexOf(attributeToPassTo);
                if(positionInStructure == 0) {
                    withStructure(structure).at(0).to(attributeToPassTo);
                } else {
                    List<AttributeVariable> attributesToSkip = structureList.subList(0, positionInStructure);
                    withStructure(structure).after(attributesToSkip).to(attributeToPassTo);
                }
            }
        }
    }
}
