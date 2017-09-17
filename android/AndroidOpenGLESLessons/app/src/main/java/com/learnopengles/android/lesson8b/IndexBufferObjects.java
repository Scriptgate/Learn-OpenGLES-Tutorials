package com.learnopengles.android.lesson8b;

import net.scriptgate.android.common.Point3D;
import net.scriptgate.android.opengles.program.Program;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static net.scriptgate.android.opengles.program.UniformVariable.TEXTURE;

class IndexBufferObjects {

    /**
     * Used for debug logs. max 23 characters
     */
    private static final String TAG = "IndexBufferObjects";
    private static final float OFFSET_BETWEEN_BUFFERS = 0.3f;

    private List<IndexBufferObject> buffers;

    private int textureHandle;

    private static final int NUMBER_OF_CUBES = 16;
    private static final int NUMBER_OF_BUFFERS = 8;
    private static final int CUBES_PER_BUFFER = NUMBER_OF_CUBES / NUMBER_OF_BUFFERS;

    IndexBufferObjects(int textureHandle) {
        this.textureHandle = textureHandle;

        buffers = new ArrayList<>();

        int halfOfTheCubes = CUBES_PER_BUFFER / 2;

        List<Cube> cubes = buildCubes();

        for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
            IndexBufferObject buffer = IndexBufferObject.allocate(CUBES_PER_BUFFER);

            List<Cube> firstHalf = cubes.subList(CUBES_PER_BUFFER * i, CUBES_PER_BUFFER * i + halfOfTheCubes);
            List<Cube> secondHalf = cubes.subList(CUBES_PER_BUFFER * i + halfOfTheCubes, CUBES_PER_BUFFER * (i + 1));
            buffer.addData(new IndexBufferObjectCreator(firstHalf));
            buffer.addData(new IndexBufferObjectCreator(secondHalf));
            buffers.add(buffer);
        }
    }

    private List<Cube> buildCubes() {


        List<Cube> cubes = new ArrayList<>();

        float heightOffset = 0;
        int indexOffset = 0;

        for (int i = 0; i < NUMBER_OF_CUBES; i++) {

            if (i % CUBES_PER_BUFFER == 0) {
                heightOffset += OFFSET_BETWEEN_BUFFERS;
            }
            cubes.add(new Cube(new Point3D(0, heightOffset, 0), indexOffset));
            heightOffset += 0.2f;
            indexOffset++;
        }
        return cubes;
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