package com.learnopengles.android.lesson8;

import android.opengl.Matrix;

import com.learnopengles.android.R;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.renderer.Renderer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.component.ProjectionMatrix.createProjectionMatrix;
import static com.learnopengles.android.common.RawResourceReader.readTextFileFromRawResource;
import static com.learnopengles.android.component.ViewMatrix.createViewInFrontOrigin;

class IndexBufferObjectRenderer implements Renderer {

	/** References to other main objects. */
	private final LessonEightActivity lessonEightActivity;


	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private final float[] modelMatrix = new float[16];

	private final ViewMatrix viewMatrix = createViewInFrontOrigin();
	private final ProjectionMatrix projectionMatrix = createProjectionMatrix(1000.0f);

	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	private final float[] mvpMatrix = new float[16];

	/** Additional matrices. */
	private final float[] accumulatedRotation = new float[16];
	private final float[] currentRotation = new float[16];
	private final float[] lightModelMatrix = new float[16];
	private final float[] temporaryMatrix = new float[16];

	/** OpenGL handles to our program uniforms. */
	private int mvpMatrixUniform;
	private int mvMatrixUniform;
	private int lightPosUniform;

	/** Identifiers for our uniforms and attributes inside the shaders. */
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
	private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";

	//TODO: These fields are used in both program and heightmap, find where they belong
	static final String POSITION_ATTRIBUTE = "a_Position";
	static final String NORMAL_ATTRIBUTE = "a_Normal";
	static final String COLOR_ATTRIBUTE = "a_Color";


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

	/** This is a handle to our cube shading program. */
	private int program;

	/** Retain the most recent delta for touch events. */
	// These still work without volatile, but refreshes are not guaranteed to
	// happen.
	volatile float deltaX;
	volatile float deltaY;

	/** The current heightmap object. */
	private HeightMap heightMap;

	/**
	 * Initialize the model data.
	 */
	IndexBufferObjectRenderer(final LessonEightActivity lessonEightActivity, ErrorHandler errorHandler) {
		this.lessonEightActivity = lessonEightActivity;
		heightMap = new HeightMap(errorHandler);
	}

	@Override
	public void onSurfaceCreated() {
		heightMap.initialize();

		// Set the background clear color to black.
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable depth testing
		glEnable(GL_DEPTH_TEST);

		viewMatrix.onSurfaceCreated();

		final String vertexShader = readTextFileFromRawResource(lessonEightActivity, R.raw.per_pixel_vertex_shader_no_tex);
		final String fragmentShader = readTextFileFromRawResource(lessonEightActivity, R.raw.per_pixel_fragment_shader_no_tex);

		final int vertexShaderHandle = ShaderHelper.compileShader(GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GL_FRAGMENT_SHADER, fragmentShader);

		program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });

		// Initialize the accumulated rotation matrix
		Matrix.setIdentityM(accumulatedRotation, 0);
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		projectionMatrix.onSurfaceChanged(width, height);
	}

	@Override
	public void onDrawFrame() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Set our per-vertex lighting program.
		glUseProgram(program);

		// Set program handles for cube drawing.
		mvpMatrixUniform = glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
		mvMatrixUniform = glGetUniformLocation(program, MV_MATRIX_UNIFORM);
		lightPosUniform = glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);


		// Calculate position of the light. Push into the distance.
		Matrix.setIdentityM(lightModelMatrix, 0);
		Matrix.translateM(lightModelMatrix, 0, 0.0f, 7.5f, -8.0f);

		Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        viewMatrix.multiplyWithVectorAndStore(lightPosInWorldSpace, lightPosInEyeSpace);

		// Draw the heightmap.
		// Translate the heightmap into the screen.
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, -12f);

		// Set a matrix that contains the current rotation.
		Matrix.setIdentityM(currentRotation, 0);
		Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(currentRotation, 0, deltaY, 1.0f, 0.0f, 0.0f);
		deltaX = 0.0f;
		deltaY = 0.0f;

		// Multiply the current rotation by the accumulated rotation, and then
		// set the accumulated rotation to the result.
		Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0, accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0, 16);

		// Rotate the cube taking the overall rotation into account.
		Matrix.multiplyMM(temporaryMatrix, 0, modelMatrix, 0, accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, modelMatrix, 0, 16);

		// This multiplies the view matrix by the model matrix, and stores
		// the result in the MVP matrix
		// (which currently contains model * view).
        viewMatrix.multiplyWithMatrixAndStore(modelMatrix, mvpMatrix);

		// Pass in the modelview matrix.
		glUniformMatrix4fv(mvMatrixUniform, 1, false, mvpMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix,
		// and stores the result in the MVP matrix
		// (which now contains model * view * projection).
		projectionMatrix.multiplyWithMatrixAndStore(mvpMatrix, temporaryMatrix);
		System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

		// Pass in the combined matrix.
		glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

		// Pass in the light position in eye space.
		glUniform3f(lightPosUniform, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

		// Render the heightmap.
		heightMap.render(program);
	}


}
