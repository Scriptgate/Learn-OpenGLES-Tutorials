package com.learnopengles.android.lesson8b;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.learnopengles.android.activity.LessonEightBActivity;
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

/**
 * This class implements our custom renderer. Note that the GL10 parameter
 * passed in is unused for OpenGL ES 2.0 renderers -- the static class GLES20 is
 * used instead.
 */
public class IndexBufferObjectRenderer implements GLSurfaceView.Renderer {

    /**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private final ModelMatrix modelMatrix = new ModelMatrix();
	private final ViewMatrix viewMatrix = createViewInFrontOrigin();
	private final ProjectionMatrix projectionMatrix = createProjectionMatrix(1000.0f);

	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	private final ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

	/** Additional matrices. */
	private final float[] lightModelMatrix = new float[16];

    /**
	 * Used to hold a light centered on the origin in model space. We need a 4th
	 * coordinate so we can get translations to work when we multiply this by
	 * our transformation matrices.
	 */
	private final float[] lightPosInModelSpace = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };

	/**
	 * Used to hold the current position of the light in world space (after
	 * transformation via model matrix).
	 */
	private final float[] lightPosInWorldSpace = new float[4];

	/**
	 * Used to hold the transformed position of the light in eye space (after
	 * transformation via modelview matrix)
	 */
	private final float[] lightPosInEyeSpace = new float[4];

	private Program program;

	/** The current heightmap object. */
	private HeightMap heightMap;

	/**
	 * Initialize the model data.
	 */
	public IndexBufferObjectRenderer(ErrorHandler errorHandler) {
        heightMap = new HeightMap(errorHandler);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		heightMap.initialize();

		// Set the background clear color to black.
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable depth testing
		glEnable(GL_DEPTH_TEST);

		viewMatrix.onSurfaceCreated();

        program = Program.createProgram("per_pixel_vertex_shader_no_tex", "per_pixel_fragment_shader_no_tex", asList(POSITION, NORMAL, COLOR));
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		projectionMatrix.onSurfaceChanged(width, height);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Set our per-vertex lighting program.
        program.useForRendering();

        // Calculate position of the light. Push into the distance.
		Matrix.setIdentityM(lightModelMatrix, 0);
		Matrix.translateM(lightModelMatrix, 0, 0.0f, 7.5f, -8.0f);

		Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        viewMatrix.multiplyWithVectorAndStore(lightPosInWorldSpace, lightPosInEyeSpace);

		// Draw the heightmap.
		// Translate the heightmap into the screen.
        modelMatrix.setIdentity();
        modelMatrix.translate(new Point3D(0.0f, 0.0f, -12f));

		// This multiplies the view matrix by the model matrix, and stores
		// the result in the MVP matrix
		// (which currently contains model * view).
        mvpMatrix.multiply(modelMatrix, viewMatrix);

		// Pass in the modelview matrix.
        mvpMatrix.passTo(program.getHandle(MV_MATRIX));

		// This multiplies the modelview matrix by the projection matrix,
		// and stores the result in the MVP matrix
		// (which now contains model * view * projection).
        mvpMatrix.multiply(projectionMatrix);

		// Pass in the combined matrix.
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

		// Pass in the light position in eye space.
		glUniform3f(program.getHandle(LIGHT_POSITION), lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

		// Render the heightmap.
		heightMap.render(program);
	}
}
