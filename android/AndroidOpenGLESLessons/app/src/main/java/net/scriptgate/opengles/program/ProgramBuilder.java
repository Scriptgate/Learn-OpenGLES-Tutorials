package net.scriptgate.opengles.program;

import android.content.Context;
import android.util.Log;

import java.util.List;

import static android.opengl.GLES20.*;
import static java.util.Arrays.asList;
import static net.scriptgate.opengles.program.ShaderHelper.compileFromResource;

public class ProgramBuilder {

    private static final String TAG = "ProgramBuilder";

    private int handle;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private List<AttributeVariable> attributes;

    public static ProgramBuilder program() {
        return new ProgramBuilder();
    }

    private ProgramBuilder() {
    }

    public ProgramBuilder withVertexShader(Context context, int vertexShaderResource) {
        return withVertexShader(compileFromResource(GL_VERTEX_SHADER, context, vertexShaderResource));
    }

    public ProgramBuilder withVertexShader(String vertexShaderResource) {
        return withVertexShader(compileFromResource(GL_VERTEX_SHADER, vertexShaderResource));
    }

    public ProgramBuilder withVertexShader(int vertexShaderHandle) {
         this.vertexShaderHandle = vertexShaderHandle;
        return this;
    }

    public ProgramBuilder withFragmentShader(Context context, int fragmentShaderResource) {
        return withFragmentShader(compileFromResource(GL_FRAGMENT_SHADER, context, fragmentShaderResource));
    }

    public ProgramBuilder withFragmentShader(String fragmentShaderResource) {
        return withFragmentShader(compileFromResource(GL_FRAGMENT_SHADER, fragmentShaderResource));
    }

    public ProgramBuilder withFragmentShader(int fragmentShaderHandle) {
        this.fragmentShaderHandle = fragmentShaderHandle;
        return this;
    }

    public ProgramBuilder withShaders(int vertexShaderHandle, int fragmentShaderHandle) {
        this.vertexShaderHandle = vertexShaderHandle;
        this.fragmentShaderHandle = fragmentShaderHandle;
        return this;
    }

    public ProgramBuilder withAttributes(AttributeVariable... attributes) {
        this.attributes = asList(attributes);
        return this;
    }

    public Program build() {

        createProgram();

        attachShader(vertexShaderHandle);
        attachShader(fragmentShaderHandle);

        bindAttributes(attributes);

        linkProgram();

        return new Program(handle);
    }

    private void createProgram() {
        handle = glCreateProgram();
        if(handle == 0) {
            throw new RuntimeException("Error creating program.");
        }
    }

    private void attachShader(int shaderHandle) {
        glAttachShader(handle, shaderHandle);
    }

    private void bindAttributes(List<AttributeVariable> attributes) {
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                glBindAttribLocation(handle, i, attributes.get(i).getName());
            }
        }
    }

    private void linkProgram() {
        // Link the two shaders together into a program.
        glLinkProgram(handle);

        // Get the link status.
        final int[] linkStatus = new int[1];
        glGetProgramiv(handle, GL_LINK_STATUS, linkStatus, 0);

        // If the link failed, delete the program.
        if (linkStatus[0] == 0) {
            Log.e(TAG, "Error compiling program: " + glGetProgramInfoLog(handle));
            glDeleteProgram(handle);
            throw new RuntimeException("Error creating program.");
        }
    }
}
