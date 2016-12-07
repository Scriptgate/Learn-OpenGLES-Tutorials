package com.learnopengles.android.lesson8;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.learnopengles.android.R;
import com.learnopengles.android.activity.LessonEightActivity;
import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.ProjectionMatrix;
import com.learnopengles.android.common.ShaderHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.ProjectionMatrix.createProjectMatrix;
import static com.learnopengles.android.common.RawResourceReader.readTextFileFromRawResource;

/**
 * This class implements our custom renderer. Note that the GL10 parameter
 * passed in is unused for OpenGL ES 2.0 renderers -- the static class GLES20 is
 * used instead.
 */
public class IndexBufferObjectRenderer implements GLSurfaceView.Renderer {

	/** References to other main objects. */
	private final LessonEightActivity lessonEightActivity;


	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private final float[] modelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	private final float[] viewMatrix = new float[16];

	private final ProjectionMatrix projectionMatrix = createProjectMatrix(1000.0f);

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
	public static final String POSITION_ATTRIBUTE = "a_Position";
	public static final String NORMAL_ATTRIBUTE = "a_Normal";
	public static final String COLOR_ATTRIBUTE = "a_Color";


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
	public volatile float deltaX;
	public volatile float deltaY;

	/** The current heightmap object. */
	private HeightMap heightMap;

	/**
	 * Initialize the model data.
	 */
	public IndexBufferObjectRenderer(final LessonEightActivity lessonEightActivity, ErrorHandler errorHandler) {
		this.lessonEightActivity = lessonEightActivity;
		heightMap = new HeightMap(errorHandler);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		heightMap.initialize();

		// Set the background clear color to black.
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable depth testing
		glEnable(GL_DEPTH_TEST);

		// Position the eye in front of the origin.
		final Point eye = new Point(0.0f,0.0f,-0.5f);

		// We are looking toward the distance
		final Point look = new Point(0.0f,0.0f,-5.0f);

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final Point up = new Point(0.0f,1.0f, 0.0f);

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination
		// of a model and view matrix. In OpenGL 2, we can keep track of these
		// matrices separately if we choose.
		Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);

		final String vertexShader = readTextFileFromRawResource(lessonEightActivity, R.raw.per_pixel_vertex_shader_no_tex);
		final String fragmentShader = readTextFileFromRawResource(lessonEightActivity, R.raw.per_pixel_fragment_shader_no_tex);

		final int vertexShaderHandle = ShaderHelper.compileShader(GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GL_FRAGMENT_SHADER, fragmentShader);

		program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });

		// Initialize the accumulated rotation matrix
		Matrix.setIdentityM(accumulatedRotation, 0);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		projectionMatrix.onSurfaceChanged(width, height);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
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
		Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);

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
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

		// Pass in the modelview matrix.
		glUniformMatrix4fv(mvMatrixUniform, 1, false, mvpMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix,
		// and stores the result in the MVP matrix
		// (which now contains model * view * projection).
		projectionMatrix.multiplyWithAndStore(mvpMatrix, temporaryMatrix);
		System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

		// Pass in the combined matrix.
		glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

		// Pass in the light position in eye space.
		glUniform3f(lightPosUniform, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

		// Render the heightmap.
		heightMap.render(program);
	}


}
