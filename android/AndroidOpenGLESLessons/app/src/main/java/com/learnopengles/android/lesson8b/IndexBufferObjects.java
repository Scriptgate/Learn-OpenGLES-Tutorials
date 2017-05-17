package com.learnopengles.android.lesson8b;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.lesson8b.IndexDataBufferFactory.createIndexData;
import static com.learnopengles.android.lesson8b.VertexDataBufferFactory.createVertexData;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;

public class IndexBufferObjects {

    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "IndexBufferObjects";

    private List<IndexBufferObject> buffers;

    private int textureHandle;

    public static final int CUBES_PER_BUFFER = 8;

    public IndexBufferObjects(int textureHandle) {
        this.textureHandle = textureHandle;

        buffers = new ArrayList<>();

        int indexOffset = 0;
        for (int i = 0; i < 2; i++) {
            IndexBufferObject buffer = IndexBufferObject.allocate();
            buffer.addData(
                    createVertexData(CUBES_PER_BUFFER, new Point3D(0, 0.2f * indexOffset, 0), indexOffset),
                    createIndexData(CUBES_PER_BUFFER)
            );
            buffers.add(buffer);
            indexOffset += CUBES_PER_BUFFER;
        }
    }

    void render(Program program) {
        setTexture(program);
        for (IndexBufferObject buffer : buffers) {
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
        final int[] buffersToDelete = new int[buffers.size() * 2];
        int index = 0;
        for (IndexBufferObject buffer : buffers) {
            buffersToDelete[index++] = buffer.vboBufferIndex;
            buffersToDelete[index++] = buffer.iboBufferIndex;
        }
        glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}