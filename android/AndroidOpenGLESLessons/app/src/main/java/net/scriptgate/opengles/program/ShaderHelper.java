package net.scriptgate.opengles.program;

import static android.opengl.GLES20.*;
import static net.scriptgate.io.RawResourceReader.readTextFromResource;

import android.content.Context;
import android.util.Log;

class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    static int compileFromResource(int shaderType, Context context, int shaderResource) {
        return compileShader(shaderType, readTextFromResource(context, shaderResource));
    }

    static int compileFromResource(int shaderType, String shaderResource) {
        return compileShader(shaderType, readTextFromResource(shaderResource));
    }

    static int compileShader(final int shaderType, final String shaderSource) {
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
                Log.e(TAG, "Error compiling shader: " + glGetShaderInfoLog(shaderHandle));
                glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }
}
