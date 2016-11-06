package com.learnopengles.android.lesson1;

import android.opengl.Matrix;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;


public class Triangle {


    private static final int BYTES_PER_FLOAT = 4;

    /**
     * How many elements per vertex.
     */
    private static final int STRIDE_BYTES = 7 * BYTES_PER_FLOAT;

    private static final int POSITION_DATA_OFFSET = 0;
    private static final int POSITION_DATA_SIZE = 3;

    private static final int COLOR_DATA_OFFSET = 3;
    private static final int COLOR_DATA_SIZE = 4;

    private final FloatBuffer vertices;
    private Point rotation = new Point();
    private Point position = new Point();

    public Triangle(float[] verticesData) {
        vertices = allocateDirect(verticesData.length * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
        vertices.put(verticesData).position(0);
    }

    public void draw(int programHandle, float[] mvpMatrix, float[] viewMatrix, float[] modelMatrix, float[] projectionMatrix) {
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
        Matrix.rotateM(modelMatrix, 0, rotation.x, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation.y, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation.z, 0.0f, 0.0f, 1.0f);

        int mvpMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        int positionHandle = glGetAttribLocation(programHandle, "a_Position");
        int colorHandle = glGetAttribLocation(programHandle, "a_Color");

        // Pass in the position information
        vertices.position(POSITION_DATA_OFFSET);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, STRIDE_BYTES, vertices);

        glEnableVertexAttribArray(positionHandle);

        // Pass in the color information
        vertices.position(COLOR_DATA_OFFSET);
        glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GL_FLOAT, false, STRIDE_BYTES, vertices);

        glEnableVertexAttribArray(colorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        glDrawArrays(GL_TRIANGLES, 0, 3);

        glDisableVertexAttribArray(positionHandle);
        glDisableVertexAttribArray(colorHandle);
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
