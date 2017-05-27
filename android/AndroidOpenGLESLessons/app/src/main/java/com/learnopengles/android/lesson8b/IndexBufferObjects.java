package com.learnopengles.android.lesson8b;

import com.learnopengles.android.BuildConfig;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.program.Program;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.lesson8b.IndexDataBufferFactory.createIndexData;
import static com.learnopengles.android.lesson8b.VertexDataBufferFactory.createPositionData;
import static com.learnopengles.android.lesson8b.VertexDataBufferFactory.createTextureData;
import static com.learnopengles.android.program.UniformVariable.TEXTURE;

public class IndexBufferObjects {

    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "IndexBufferObjects";
    public static final float OFFSET_BETWEEN_BUFFERS = 0.3f;

    private List<IndexBufferObject> buffers;

    private int textureHandle;

    private static final int NUMBER_OF_CUBES =16;
    private static final int NUMBER_OF_BUFFERS = 8;

    public IndexBufferObjects(int textureHandle) {
        this.textureHandle = textureHandle;

        buffers = new ArrayList<>();
        int cubesPerBuffer = NUMBER_OF_CUBES / NUMBER_OF_BUFFERS;
        int halfOfTheCubes = cubesPerBuffer / 2;

        Point3D offset = new Point3D();
        int indexOffset = 0;
        for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
            IndexBufferObject buffer = IndexBufferObject.allocate(cubesPerBuffer);
            buffer.addData(
                    createPositionData(halfOfTheCubes, offset),
                    createTextureData(halfOfTheCubes, indexOffset),
                    createIndexData(halfOfTheCubes, (short) 0)
            );
            offset.y += halfOfTheCubes * 0.2f;
            indexOffset += halfOfTheCubes;
            short indicesOffset = (short) (7*halfOfTheCubes);
            buffer.addData(
                    createPositionData(halfOfTheCubes, offset),
                    createTextureData(halfOfTheCubes, indexOffset),
                    createIndexData(halfOfTheCubes, indicesOffset)
            );
            offset.y += halfOfTheCubes * 0.2f + OFFSET_BETWEEN_BUFFERS;
            indexOffset += halfOfTheCubes;
            buffers.add(buffer);
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