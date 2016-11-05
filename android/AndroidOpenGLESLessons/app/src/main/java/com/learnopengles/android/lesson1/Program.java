package com.learnopengles.android.lesson1;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindAttribLocation;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glLinkProgram;
import static com.learnopengles.android.lesson1.Shader.loadShader;

public class Program {

    public static int createProgram(String vertexShaderResource, String fragmentShaderResource) {

        int vertexShaderHandle = loadShader(GL_VERTEX_SHADER, vertexShaderResource);
        int fragmentShaderHandle = loadShader(GL_FRAGMENT_SHADER, fragmentShaderResource);

        int programHandle = glCreateProgram();

        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            glBindAttribLocation(programHandle, 0, "a_Position");
            glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            glGetProgramiv(programHandle, GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

}
