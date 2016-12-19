package com.learnopengles.android.lesson4;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.data.CubeDataCollection;
import com.learnopengles.android.program.Program;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static com.learnopengles.android.program.AttributeVariable.COLOR;
import static com.learnopengles.android.program.AttributeVariable.NORMAL;
import static com.learnopengles.android.program.AttributeVariable.POSITION;
import static com.learnopengles.android.program.AttributeVariable.TEXTURE_COORDINATE;
import static com.learnopengles.android.program.UniformVariable.LIGHT_POSITION;
import static com.learnopengles.android.program.UniformVariable.MVP_MATRIX;
import static com.learnopengles.android.program.UniformVariable.MV_MATRIX;

public class Cube {

    private CubeDataCollection cubeData;
    private Point3D position = new Point3D();
    private Point3D rotation = new Point3D();

    public Cube(CubeDataCollection cubeData, Point3D point) {
        this.cubeData = cubeData;
        this.position = point;
    }

    public void drawCube(Program program, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light light) {
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
        cubeData.passPositionTo(positionHandle);

        // Pass in the color information
        cubeData.passColorTo(colorHandle);

        // Pass in the normal information
        cubeData.passNormalTo(normalHandle);

        // Pass in the texture coordinate information
        cubeData.passTextureTo(textureCoordinateHandle);

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
