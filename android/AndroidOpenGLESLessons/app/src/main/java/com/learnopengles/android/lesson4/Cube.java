package com.learnopengles.android.lesson4;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.AttributeVariable;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.program.UniformVariable;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;
import static com.learnopengles.android.program.UniformVariable.LIGHT_POSITION;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;
import static com.learnopengles.android.program.UniformVariable.MV_MATRIX;

public class Cube {

    private static final int POSITION_DATA_SIZE = 3;
    private static final int COLOR_DATA_SIZE = 4;
    private static final int NORMAL_DATA_SIZE = 3;
    private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;

    private Point3D position = new Point3D();
    private Point3D rotation = new Point3D();

    public Cube(Point3D point) {
        this.position = point;
    }

    public void drawCube(Program program, FloatBuffer cubePositions, FloatBuffer cubeColors, FloatBuffer cubeNormals, FloatBuffer cubeTextureCoordinates, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light light) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        // Set program handles for cube drawing.
        int mvpMatrixHandle = program.getHandle(MVP_MATRIX);
        int mvMatrixHandle = program.getHandle(MV_MATRIX);
        int lightPosHandle = program.getHandle(LIGHT_POSITION);
        int positionHandle = program.getHandle(POSITION);
        int colorHandle = program.getHandle(COLOR);
        int normalHandle = program.getHandle(NORMAL);
        int textureCoordinateHandle = program.getHandle(TEXTURE_COORDINATE);

        // Pass in the position information
        cubePositions.position(0);
        glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, cubePositions);

        glEnableVertexAttribArray(positionHandle);

        // Pass in the color information
        cubeColors.position(0);
        glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GL_FLOAT, false, 0, cubeColors);

        glEnableVertexAttribArray(colorHandle);

        // Pass in the normal information
        cubeNormals.position(0);
        glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, cubeNormals);

        glEnableVertexAttribArray(normalHandle);

        // Pass in the texture coordinate information
        cubeTextureCoordinates.position(0);
        glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, cubeTextureCoordinates);

        glEnableVertexAttribArray(textureCoordinateHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        mvpMatrix.multiply(modelMatrix, viewMatrix);

        // Pass in the modelview matrix.
        mvpMatrix.passTo(mvMatrixHandle);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix);

        // Pass in the combined matrix.
        mvpMatrix.passTo(mvpMatrixHandle);

        // Pass in the light position in eye space.
        light.passTo(lightPosHandle);

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);
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

    public void setPosition(Point3D position) {
        this.position = position;
    }

}
