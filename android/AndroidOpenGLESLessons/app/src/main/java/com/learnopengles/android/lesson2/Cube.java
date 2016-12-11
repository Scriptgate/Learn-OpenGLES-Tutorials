package com.learnopengles.android.lesson2;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;

public class Cube {

    private final CubeData cubeData;
    private Point3D position = new Point3D();
    private Point3D rotation = new Point3D();

    public Cube(CubeData cubeData, Point3D point) {
        this.cubeData = cubeData;
        this.position = point;
    }

    /**
     * Draws a cube.
     */
    public void drawCube(int perVertexProgramHandle, ModelViewProjectionMatrix mvpMatrix, ModelMatrix modelMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, Light lightPosInEyeSpace) {
        modelMatrix.setIdentity();

        modelMatrix.translate(position);
        modelMatrix.rotate(rotation);

        // Set program handles for cube drawing.
        int positionHandle = glGetAttribLocation(perVertexProgramHandle, "a_Position");
        int colorHandle = glGetAttribLocation(perVertexProgramHandle, "a_Color");
        int normalHandle = glGetAttribLocation(perVertexProgramHandle, "a_Normal");

        // Pass in the position information
        cubeData.passPositionTo(positionHandle);

        // Pass in the color information
        cubeData.passColorTo(colorHandle);

        // Pass in the normal information
        cubeData.passNormalTo(normalHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        mvpMatrix.multiply(modelMatrix, viewMatrix);
        // Pass in the modelview matrix.
        mvpMatrix.passTo(glGetUniformLocation(perVertexProgramHandle, "u_MVMatrix"));

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix);
        // Pass in the combined matrix.
        mvpMatrix.passTo(glGetUniformLocation(perVertexProgramHandle, "u_MVPMatrix"));

        // Pass in the light position in eye space.
        lightPosInEyeSpace.passTo(glGetUniformLocation(perVertexProgramHandle, "u_LightPos"));

        // Draw the cube.
        glDrawArrays(GL_TRIANGLES, 0, 36);

        //TODO: Still not sure when I should call glDisableVertexAttribArray, disabling normalHandle seems to cause problems:
        //TODO: glDrawArrays: no data bound to the command - ignoring
        glDisableVertexAttribArray(positionHandle);
        glDisableVertexAttribArray(colorHandle);
//        glDisableVertexAttribArray(normalHandle);
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
