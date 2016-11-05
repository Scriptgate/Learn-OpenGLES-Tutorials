package com.learnopengles.android.lesson1;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glShaderSource;
import static com.learnopengles.android.common.RawResourceReader.readShaderFileFromResource;

public class Shader {

    public static int loadShader(int shaderType, String resource) {
        final String shaderSource = readShaderFileFromResource(resource);
        int shaderHandle = glCreateShader(shaderType);

        if (shaderHandle != 0) {
            // Pass in the shader source.
            glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            glGetShaderiv(shaderHandle, GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0) {
            throw new RuntimeException("Error creating shader: " + resource);
        }
        return shaderHandle;
    }
}
