package com.learnopengles.android.lesson1;

import android.opengl.Matrix;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;


public class Triangle {

    /**
     * How many bytes per float.
     */
    private static final int BYTES_PER_FLOAT = 4;

    /**
     * How many elements per vertex.
     */
    private static final int STRIDE_BYTES = 7 * BYTES_PER_FLOAT;

    /**
     * Offset of the position data.
     */
    private static final int POSITION_OFFSET = 0;

    /**
     * Size of the position data in elements.
     */
    private static final int POSITION_DATA_SIZE = 3;

    /**
     * Offset of the color data.
     */
    private static final int COLOR_OFFSET = 3;

    /**
     * Size of the color data in elements.
     */
    private static final int COLOR_DATA_SIZE = 4;

    private final FloatBuffer vertices;
    private Point rotation = new Point();
    private Point position = new Point();

    public Triangle(float[] verticesData) {
        vertices = allocateDirect(verticesData.length * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
        vertices.put(verticesData).position(0);
    }

    public void draw(int programHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix) {
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, position.x, position.y, position.z);
        Matrix.rotateM(mModelMatrix, 0, rotation.x, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, rotation.y, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, rotation.z, 0.0f, 0.0f, 1.0f);

        int mMVPMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        int mPositionHandle = glGetAttribLocation(programHandle, "a_Position");
        int mColorHandle = glGetAttribLocation(programHandle, "a_Color");

        // Pass in the position information
        vertices.position(POSITION_OFFSET);
        glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, STRIDE_BYTES, vertices);

        glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        vertices.position(COLOR_OFFSET);
        glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GL_FLOAT, false, STRIDE_BYTES, vertices);

        glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setRotationX(float rotation) {
        this.rotation.x = rotation;
    }

    public void setRotationY(float rotation) {
        this.rotation.y = rotation;
    }

    public void setRotationZ(float rotation) {
        this.rotation.z = rotation;
    }
}
