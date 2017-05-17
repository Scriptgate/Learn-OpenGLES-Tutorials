package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.lesson8b.IndexDataBufferFactory.createIndexData;
import static com.learnopengles.android.lesson8b.VertexDataBufferFactory.createVertexData;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;
import static java.util.Arrays.asList;

public class IndexBufferObjects {

    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "IndexBufferObjects";

    private IndexBufferObject bufferA;
    private IndexBufferObject bufferB;

    private int textureHandle;

    public static final int CUBES_PER_BUFFER = 8;

    public IndexBufferObjects(int textureHandle) {
        this.textureHandle = textureHandle;

        bufferA = IndexBufferObject.allocate();
        bufferB = IndexBufferObject.allocate();

        int indexOffset = 0;
        bufferA.addData(
                createVertexData(CUBES_PER_BUFFER, new Point3D(), indexOffset),
                createIndexData(CUBES_PER_BUFFER)
        );
        indexOffset += CUBES_PER_BUFFER;
        bufferB.addData(
                createVertexData(CUBES_PER_BUFFER, new Point3D(0, 0.2f * indexOffset, 0), indexOffset),
                createIndexData(CUBES_PER_BUFFER)
        );
    }

    void render(Program program) {
        setTexture(program);
        for (IndexBufferObject buffer : asList(bufferA, bufferB)) {
            buffer.render(program);
        }
    }

    private void setTexture(Program program) {
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(program.getHandle(TEXTURE), 0);
    }

    void release() {
        final int[] buffersToDelete = new int[]{bufferA.vboBufferIndex, bufferA.iboBufferIndex, bufferB.vboBufferIndex, bufferB.iboBufferIndex};
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}