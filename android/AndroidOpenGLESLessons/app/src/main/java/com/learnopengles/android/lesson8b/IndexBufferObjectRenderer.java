package com.learnopengles.android.lesson8b;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.program.Program;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.UniformVariable.*;
import static java.util.Arrays.asList;

public class IndexBufferObjectRenderer implements GLSurfaceView.Renderer {

	private final ModelMatrix modelMatrix = new ModelMatrix();
	private final ViewMatrix viewMatrix = createViewInFrontOrigin();
	private final ProjectionMatrix projectionMatrix = createProjectionMatrix(1000.0f);
	private final ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

	private Program program;

	private HeightMap heightMap;

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		heightMap = new HeightMap();

		// Set the background clear color to black.
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		glEnable(GL_DEPTH_TEST);

		viewMatrix.onSurfaceCreated();

        program = Program.createProgram("per_pixel_vertex_shader_position_color", "per_pixel_fragment_shader_position_color", asList(POSITION, COLOR));
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		projectionMatrix.onSurfaceChanged(width, height);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        program.useForRendering();

        modelMatrix.setIdentity();
		// Translate the heightmap into the screen.
        modelMatrix.translate(new Point3D(0.0f, 0.0f, -12f));

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

		// Render the heightmap.
        if(heightMap != null) {
            heightMap.render(program);
        }
	}
}
