package com.learnopengles.android.lesson7;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.learnopengles.android.R;
import com.learnopengles.android.activity.LessonSevenActivity;
import com.learnopengles.android.common.Point;
import com.learnopengles.android.common.CubeBuilder;

import java.nio.FloatBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.FloatBufferHelper.BYTES_PER_FLOAT;
import static com.learnopengles.android.common.FloatBufferHelper.allocateBuffer;
import static com.learnopengles.android.common.RawResourceReader.readTextFileFromRawResource;
import static com.learnopengles.android.common.ShaderHelper.compileShader;
import static com.learnopengles.android.common.ShaderHelper.createAndLinkProgram;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

/**
 * This class implements our custom renderer. Note that the GL10 parameter
 * passed in is unused for OpenGL ES 2.0 renderers -- the static class GLES20 is
 * used instead.
 */
public class VertexBufferObjectRenderer implements GLSurfaceView.Renderer {
	/** Used for debug logs. */
	private static final String TAG = "VertexBufferObjectRenderer";

	private final LessonSevenActivity lessonSevenActivity;
	private final GLSurfaceView glSurfaceView;
	
	/**
	 * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
	 * of being located at the center of the universe) to world space.
	 */
	private float[] modelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	 * it positions things relative to our eye.
	 */
	private float[] viewMatrix = new float[16];

	/** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	private float[] projectionMatrix = new float[16];
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mvpMatrix = new float[16];
	
	/** Store the accumulated rotation. */
	private final float[] accumulatedRotation = new float[16];
	
	/** Store the current rotation. */
	private final float[] currentRotation = new float[16];
	
	/** A temporary matrix. */
	private float[] temporaryMatrix = new float[16];
	
	/** 
	 * Stores a copy of the model matrix specifically for the light position.
	 */
	private float[] lightModelMatrix = new float[16];
	
	/** This will be used to pass in the transformation matrix. */
	private int mvpMatrixHandle;
	
	/** This will be used to pass in the modelview matrix. */
	private int mvMatrixHandle;
	
	/** This will be used to pass in the light position. */
	private int lightPosHandle;
	
	/** This will be used to pass in the texture. */
	private int textureUniformHandle;
	
	/** This will be used to pass in model position information. */
	private int positionHandle;
	
	/** This will be used to pass in model normal information. */
	private int normalHandle;
	
	/** This will be used to pass in model texture coordinate information. */
	private int textureCoordinateHandle;
	
	/** Additional info for cube generation. */
	private int lastRequestedCubeFactor;
	private int actualCubeFactor;
	
	/** Control whether vertex buffer objects or client-side memory will be used for rendering. */
	private boolean useVBOs = true;
	
	/** Control whether strides will be used. */
	private boolean useStride = true;
	
	/** Size of the position data in elements. */
	static final int POSITION_DATA_SIZE = 3;	
	
	/** Size of the normal data in elements. */
	static final int NORMAL_DATA_SIZE = 3;
	
	/** Size of the texture coordinate data in elements. */
	static final int TEXTURE_COORDINATE_DATA_SIZE = 2;
	
	/** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
	 *  we multiply this by our transformation matrices. */
	private final float[] lightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	
	/** Used to hold the current position of the light in world space (after transformation via model matrix). */
	private final float[] lightPosInWorldSpace = new float[4];
	
	/** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
	private final float[] lightPosInEyeSpace = new float[4];
	
	/** This is a handle to our cube shading program. */
	private int programHandle;
	
	/** These are handles to our texture data. */
	private int androidDataHandle;
	
	// These still work without volatile, but refreshes are not guaranteed to happen.					
	public volatile float deltaX;
	public volatile float deltaY;
	
	/** Thread executor for generating cube data in the background. */
	private final ExecutorService singleThreadedExecutor = Executors.newSingleThreadExecutor();
	
	/** The current cubes object. */
	private Cubes cubes;

	/**
	 * Initialize the model data.
	 */
	public VertexBufferObjectRenderer(final LessonSevenActivity lessonSevenActivity, final GLSurfaceView glSurfaceView) {
		this.lessonSevenActivity = lessonSevenActivity;
		this.glSurfaceView = glSurfaceView;
	}

	private void generateCubes(int cubeFactor, boolean toggleVbos, boolean toggleStride) {
		singleThreadedExecutor.submit(new GenDataRunnable(cubeFactor, toggleVbos, toggleStride));
	}
	
	class GenDataRunnable implements Runnable {
		final int requestedCubeFactor;
		final boolean toggleVBOs;
		final boolean toggleStride;
		
		GenDataRunnable(int requestedCubeFactor, boolean toggleVBOs, boolean toggleStride) {
			this.requestedCubeFactor = requestedCubeFactor;
			this.toggleVBOs = toggleVBOs;
			this.toggleStride = toggleStride;
		}
		
		@Override
		public void run() {			
			try {
				// X, Y, Z
				// The normal is used in light calculations and is a vector which points
				// orthogonal to the plane of the surface. For a cube model, the normals
				// should be orthogonal to the points of each face.
				final float[] cubeNormalData =
				{												
						// Front face
						0.0f, 0.0f, 1.0f,				
						0.0f, 0.0f, 1.0f,
						0.0f, 0.0f, 1.0f,
						0.0f, 0.0f, 1.0f,				
						0.0f, 0.0f, 1.0f,
						0.0f, 0.0f, 1.0f,
						
						// Right face 
						1.0f, 0.0f, 0.0f,				
						1.0f, 0.0f, 0.0f,
						1.0f, 0.0f, 0.0f,
						1.0f, 0.0f, 0.0f,				
						1.0f, 0.0f, 0.0f,
						1.0f, 0.0f, 0.0f,
						
						// Back face 
						0.0f, 0.0f, -1.0f,				
						0.0f, 0.0f, -1.0f,
						0.0f, 0.0f, -1.0f,
						0.0f, 0.0f, -1.0f,				
						0.0f, 0.0f, -1.0f,
						0.0f, 0.0f, -1.0f,
						
						// Left face 
						-1.0f, 0.0f, 0.0f,				
						-1.0f, 0.0f, 0.0f,
						-1.0f, 0.0f, 0.0f,
						-1.0f, 0.0f, 0.0f,				
						-1.0f, 0.0f, 0.0f,
						-1.0f, 0.0f, 0.0f,
						
						// Top face 
						0.0f, 1.0f, 0.0f,			
						0.0f, 1.0f, 0.0f,
						0.0f, 1.0f, 0.0f,
						0.0f, 1.0f, 0.0f,				
						0.0f, 1.0f, 0.0f,
						0.0f, 1.0f, 0.0f,
						
						// Bottom face 
						0.0f, -1.0f, 0.0f,			
						0.0f, -1.0f, 0.0f,
						0.0f, -1.0f, 0.0f,
						0.0f, -1.0f, 0.0f,				
						0.0f, -1.0f, 0.0f,
						0.0f, -1.0f, 0.0f
				};
				
				// S, T (or X, Y)
				// Texture coordinate data.
				// Because images have a Y axis pointing downward (values increase as you move down the image) while
				// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
				// What's more is that the texture coordinates are the same for every face.
				final float[] cubeTextureCoordinateData =
				{												
						// Front face
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,				
						
						// Right face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	
						
						// Back face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	
						
						// Left face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	
						
						// Top face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	
						
						// Bottom face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f
				};		
							
				final float[] cubePositionData = new float[108 * requestedCubeFactor * requestedCubeFactor * requestedCubeFactor];
				int cubePositionDataOffset = 0;
									
				final int segments = requestedCubeFactor + (requestedCubeFactor - 1);
				final float minPosition = -1.0f;
				final float maxPosition = 1.0f;
				final float positionRange = maxPosition - minPosition;
				
				for (int x = 0; x < requestedCubeFactor; x++) {
					for (int y = 0; y < requestedCubeFactor; y++) {
						for (int z = 0; z < requestedCubeFactor; z++) {
							final float x1 = minPosition + ((positionRange / segments) * (x * 2));
							final float x2 = minPosition + ((positionRange / segments) * ((x * 2) + 1));
							
							final float y1 = minPosition + ((positionRange / segments) * (y * 2));
							final float y2 = minPosition + ((positionRange / segments) * ((y * 2) + 1));
							
							final float z1 = minPosition + ((positionRange / segments) * (z * 2));
							final float z2 = minPosition + ((positionRange / segments) * ((z * 2) + 1));
							
							// Define points for a cube.
							// X, Y, Z
							final Point p1p = new Point(x1, y2, z2 );
							final Point p2p = new Point(x2, y2, z2 );
							final Point p3p = new Point(x1, y1, z2 );
							final Point p4p = new Point(x2, y1, z2 );
							final Point p5p = new Point(x1, y2, z1 );
							final Point p6p = new Point(x2, y2, z1 );
							final Point p7p = new Point(x1, y1, z1 );
							final Point p8p = new Point(x2, y1, z1 );

							final float[] thisCubePositionData = CubeBuilder.generatePositionData(p1p, p2p, p3p, p4p, p5p, p6p, p7p, p8p);
							
							System.arraycopy(thisCubePositionData, 0, cubePositionData, cubePositionDataOffset, thisCubePositionData.length);
							cubePositionDataOffset += thisCubePositionData.length;
						}
					}
				}					
				
				// Run on the GL thread -- the same thread the other members of the renderer run in.
				glSurfaceView.queueEvent(new Runnable() {
					@Override
					public void run() {												
						if (cubes != null) {
							cubes.release();
							cubes = null;
						}
						
						// Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
						System.gc();
						
						try {
							boolean useVbos = useVBOs;
							boolean useStride = VertexBufferObjectRenderer.this.useStride;
							
							if (toggleVBOs) {
								useVbos = !useVbos;
							}
							
							if (toggleStride) {
								useStride = !useStride;
							}
							
							if (useStride) {
								if (useVbos) {
									cubes = new CubesWithVboWithStride(cubePositionData, cubeNormalData, cubeTextureCoordinateData, requestedCubeFactor);
								} else {
									cubes = new CubesClientSideWithStride(cubePositionData, cubeNormalData, cubeTextureCoordinateData, requestedCubeFactor);
								}
							} else {
								if (useVbos) {
									cubes = new CubesWithVbo(cubePositionData, cubeNormalData, cubeTextureCoordinateData, requestedCubeFactor);
								} else {
									cubes = new CubesClientSide(cubePositionData, cubeNormalData, cubeTextureCoordinateData, requestedCubeFactor);
								}
							}	
																			
							useVBOs = useVbos;
							lessonSevenActivity.updateVboStatus(useVBOs);
						
							VertexBufferObjectRenderer.this.useStride = useStride;
							lessonSevenActivity.updateStrideStatus(VertexBufferObjectRenderer.this.useStride);
							
							actualCubeFactor = requestedCubeFactor;
						} catch (OutOfMemoryError err) {
							if (cubes != null) {
								cubes.release();
								cubes = null;
							}
							
							// Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
							System.gc();
							
							lessonSevenActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
//									Toast.makeText(lessonSevenActivity, "Out of memory; Dalvik takes a while to clean up the memory. Please try again.\nExternal bytes allocated=" + dalvik.system.VMRuntime.getRuntime().getExternalBytesAllocated(), Toast.LENGTH_LONG).show();
								}
							});										
						}																	
					}				
				});
			} catch (OutOfMemoryError e) {
				// Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
				System.gc();
				
				lessonSevenActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						Toast.makeText(lessonSevenActivity, "Out of memory; Dalvik takes a while to clean up the memory. Please try again.\nExternal bytes allocated=" + dalvik.system.VMRuntime.getRuntime().getExternalBytesAllocated(), Toast.LENGTH_LONG).show();
					}
				});
			}			
		}
	}

	public void decreaseCubeCount() {
		if (lastRequestedCubeFactor > 1) {
			generateCubes(--lastRequestedCubeFactor, false, false);
		}
	}

	public void increaseCubeCount() {
		if (lastRequestedCubeFactor < 16) {
			generateCubes(++lastRequestedCubeFactor, false, false);
		}
	}	
	
	public void toggleVBOs() {		
		generateCubes(lastRequestedCubeFactor, true, false);
	}
	
	public void toggleStride() {
		generateCubes(lastRequestedCubeFactor, false, true);
	}
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{		
		lastRequestedCubeFactor = actualCubeFactor = 3;
		generateCubes(actualCubeFactor, false, false);
		
		// Set the background clear color to black.
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// Use culling to remove back faces.
		glEnable(GL_CULL_FACE);
		
		// Enable depth testing
		glEnable(GL_DEPTH_TEST);
		
		// Position the eye in front of the origin.
		final Point eye = new Point(0.0f,0.0f,-0.5f);

		// We are looking toward the distance
		final Point look = new Point(0.0f,0.0f,-5.0f);

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final Point up = new Point(0.0f,1.0f, 0.0f);

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);

		final String vertexShader = readTextFileFromRawResource(lessonSevenActivity, R.raw.lesson_seven_vertex_shader);
 		final String fragmentShader = readTextFileFromRawResource(lessonSevenActivity, R.raw.lesson_seven_fragment_shader);
 				
		final int vertexShaderHandle = compileShader(GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, fragmentShader);
		
		programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {"a_Position",  "a_Normal", "a_TexCoordinate"});
        
		// Load the texture
		androidDataHandle = loadTexture(lessonSevenActivity, R.drawable.usb_android);
		glGenerateMipmap(GL_TEXTURE_2D);
		
		glBindTexture(GL_TEXTURE_2D, androidDataHandle);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		glBindTexture(GL_TEXTURE_2D, androidDataHandle);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        
        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(accumulatedRotation, 0);
	}	
		
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		// Set the OpenGL viewport to the same size as the surface.
		glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 1000.0f;
		
		Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
	}	

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Set our per-vertex lighting program.
        glUseProgram(programHandle);
        
        // Set program handles for cube drawing.
        mvpMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        mvMatrixHandle = glGetUniformLocation(programHandle, "u_MVMatrix");
        lightPosHandle = glGetUniformLocation(programHandle, "u_LightPos");
        textureUniformHandle = glGetUniformLocation(programHandle, "u_Texture");
        positionHandle = glGetAttribLocation(programHandle, "a_Position");
        normalHandle = glGetAttribLocation(programHandle, "a_Normal");
        textureCoordinateHandle = glGetAttribLocation(programHandle, "a_TexCoordinate");
        
        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -1.0f);
               
        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);
        
        // Draw a cube.
        // Translate the cube into the screen.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, -3.5f);
        
        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(currentRotation, 0);
    	Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
    	Matrix.rotateM(currentRotation, 0, deltaY, 1.0f, 0.0f, 0.0f);
    	deltaX = 0.0f;
    	deltaY = 0.0f;
    	    	
    	// Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
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
		glUniformMatrix4fv(mvMatrixHandle, 1, false, mvpMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix,
		// and stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(temporaryMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

		// Pass in the combined matrix.
		glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

		// Pass in the light position in eye space.
		glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
		
		// Pass in the texture information
		// Set the active texture unit to texture unit 0.
		glActiveTexture(GL_TEXTURE0);

		// Bind the texture to this unit.
		glBindTexture(GL_TEXTURE_2D, androidDataHandle);

		// Tell the texture uniform sampler to use this texture in the
		// shader by binding to texture unit 0.
		glUniform1i(textureUniformHandle, 0);
        
		if (cubes != null) {
			cubes.render();
		}
	}		
	
	abstract class Cubes {
		abstract void render();

		abstract void release();	
		
		FloatBuffer[] getBuffers(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
			// First, copy cube information into client-side floating point buffers.
			final FloatBuffer cubePositionsBuffer;
			final FloatBuffer cubeNormalsBuffer;
			final FloatBuffer cubeTextureCoordinatesBuffer;
			
			cubePositionsBuffer = allocateBuffer(cubePositions);
			
            int generatedCubeFactorToPowerThree = generatedCubeFactor * generatedCubeFactor * generatedCubeFactor;
			cubeNormalsBuffer = allocateDirect(cubeNormals.length * BYTES_PER_FLOAT * generatedCubeFactorToPowerThree).order(nativeOrder()).asFloatBuffer();

            for (int i = 0; i < generatedCubeFactorToPowerThree; i++) {
				cubeNormalsBuffer.put(cubeNormals);
			}
						
			cubeNormalsBuffer.position(0);
			
			cubeTextureCoordinatesBuffer = allocateDirect(cubeTextureCoordinates.length * BYTES_PER_FLOAT * generatedCubeFactorToPowerThree).order(nativeOrder()).asFloatBuffer();
			
			for (int i = 0; i < generatedCubeFactorToPowerThree; i++) {
				cubeTextureCoordinatesBuffer.put(cubeTextureCoordinates);
			}
			
			cubeTextureCoordinatesBuffer.position(0);
			
			return new FloatBuffer[] {cubePositionsBuffer, cubeNormalsBuffer, cubeTextureCoordinatesBuffer};
		}		
		
		FloatBuffer getInterleavedBuffer(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
			final int cubeDataLength = cubePositions.length 
	                 + (cubeNormals.length * generatedCubeFactor * generatedCubeFactor * generatedCubeFactor) 
	                 + (cubeTextureCoordinates.length * generatedCubeFactor * generatedCubeFactor * generatedCubeFactor);			
			int cubePositionOffset = 0;
			int cubeNormalOffset = 0;
			int cubeTextureOffset = 0;
			
			final FloatBuffer cubeBuffer = allocateDirect(cubeDataLength * BYTES_PER_FLOAT).order(nativeOrder()).asFloatBuffer();
			
			for (int i = 0; i < generatedCubeFactor * generatedCubeFactor * generatedCubeFactor; i++) {								
				for (int v = 0; v < 36; v++) {
					cubeBuffer.put(cubePositions, cubePositionOffset, POSITION_DATA_SIZE);
					cubePositionOffset += POSITION_DATA_SIZE;
					cubeBuffer.put(cubeNormals, cubeNormalOffset, NORMAL_DATA_SIZE);
					cubeNormalOffset += NORMAL_DATA_SIZE;
					cubeBuffer.put(cubeTextureCoordinates, cubeTextureOffset, TEXTURE_COORDINATE_DATA_SIZE);
					cubeTextureOffset += TEXTURE_COORDINATE_DATA_SIZE;					
				}
				
				// The normal and texture data is repeated for each cube.
				cubeNormalOffset = 0;
				cubeTextureOffset = 0;	
			}
			
			cubeBuffer.position(0);
			
			return cubeBuffer;
		}
	}
	
	class CubesClientSide extends Cubes {
		private FloatBuffer cubePositions;
		private FloatBuffer cubeNormals;
		private FloatBuffer cubeTextureCoordinates;

		CubesClientSide(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {	
			FloatBuffer[] buffers = getBuffers(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);
			
			this.cubePositions = buffers[0];
			this.cubeNormals = buffers[1];
			this.cubeTextureCoordinates = buffers[2];
		}

		@Override
		public void render() {				        
			// Pass in the position information
			glEnableVertexAttribArray(positionHandle);
			glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, cubePositions);

			// Pass in the normal information
			glEnableVertexAttribArray(normalHandle);
			glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, cubeNormals);
			
			// Pass in the texture information
			glEnableVertexAttribArray(textureCoordinateHandle);
			glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, cubeTextureCoordinates);

			// Draw the cubes.
			glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
		}

		@Override
		public void release() {
			cubePositions.limit(0);
			cubePositions = null;
			cubeNormals.limit(0);
			cubeNormals = null;
			cubeTextureCoordinates.limit(0);
			cubeTextureCoordinates = null;
		}
	}
	
	class CubesClientSideWithStride extends Cubes {
		private FloatBuffer cubeBuffer;

		CubesClientSideWithStride(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {	
			cubeBuffer = getInterleavedBuffer(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);
		}

		@Override
		public void render() {				
			final int stride = (POSITION_DATA_SIZE + NORMAL_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE) * BYTES_PER_FLOAT;
			
			// Pass in the position information
			cubeBuffer.position(0);
			glEnableVertexAttribArray(positionHandle);
			glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, stride, cubeBuffer);

			// Pass in the normal information
			cubeBuffer.position(POSITION_DATA_SIZE);
			glEnableVertexAttribArray(normalHandle);
			glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, stride, cubeBuffer);
			
			// Pass in the texture information
			cubeBuffer.position(POSITION_DATA_SIZE + NORMAL_DATA_SIZE);
			glEnableVertexAttribArray(textureCoordinateHandle);
			glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, stride, cubeBuffer);

			// Draw the cubes.
			glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
		}

		@Override
		public void release() {
			cubeBuffer.limit(0);
			cubeBuffer = null;
		}
	}
	
	class CubesWithVbo extends Cubes {
		final int cubePositionsBufferIdx;
		final int cubeNormalsBufferIdx;
		final int cubeTexCoordsBufferIdx;

		CubesWithVbo(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
			FloatBuffer[] floatBuffers = getBuffers(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);
			
			FloatBuffer cubePositionsBuffer = floatBuffers[0];
			FloatBuffer cubeNormalsBuffer = floatBuffers[1];
			FloatBuffer cubeTextureCoordinatesBuffer = floatBuffers[2];			
			
			// Second, copy these buffers into OpenGL's memory. After, we don't need to keep the client-side buffers around.					
			final int buffers[] = new int[3];
			glGenBuffers(3, buffers, 0);

			glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
			glBufferData(GL_ARRAY_BUFFER, cubePositionsBuffer.capacity() * BYTES_PER_FLOAT, cubePositionsBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);
			glBufferData(GL_ARRAY_BUFFER, cubeNormalsBuffer.capacity() * BYTES_PER_FLOAT, cubeNormalsBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, buffers[2]);
			glBufferData(GL_ARRAY_BUFFER, cubeTextureCoordinatesBuffer.capacity() * BYTES_PER_FLOAT, cubeTextureCoordinatesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);

			cubePositionsBufferIdx = buffers[0];
			cubeNormalsBufferIdx = buffers[1];
			cubeTexCoordsBufferIdx = buffers[2];
			
			cubePositionsBuffer.limit(0);
			cubePositionsBuffer = null;
			cubeNormalsBuffer.limit(0);
			cubeNormalsBuffer = null;
			cubeTextureCoordinatesBuffer.limit(0);
			cubeTextureCoordinatesBuffer = null;
		}

		@Override
		public void render() {	      
			// Pass in the position information
			glBindBuffer(GL_ARRAY_BUFFER, cubePositionsBufferIdx);
			glEnableVertexAttribArray(positionHandle);
			glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, 0, 0);

			// Pass in the normal information
			glBindBuffer(GL_ARRAY_BUFFER, cubeNormalsBufferIdx);
			glEnableVertexAttribArray(normalHandle);
			glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, 0, 0);
			
			// Pass in the texture information
			glBindBuffer(GL_ARRAY_BUFFER, cubeTexCoordsBufferIdx);
			glEnableVertexAttribArray(textureCoordinateHandle);
			glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, 0, 0);

			// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			// Draw the cubes.
			glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
		}

		@Override
		public void release() {
			// Delete buffers from OpenGL's memory
			final int[] buffersToDelete = new int[] {cubePositionsBufferIdx, cubeNormalsBufferIdx, cubeTexCoordsBufferIdx};
			glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
		}
	}
	
	class CubesWithVboWithStride extends Cubes {
		final int cubeBufferIdx;

		CubesWithVboWithStride(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int generatedCubeFactor) {
			FloatBuffer cubeBuffer = getInterleavedBuffer(cubePositions, cubeNormals, cubeTextureCoordinates, generatedCubeFactor);			
			
			// Second, copy these buffers into OpenGL's memory. After, we don't need to keep the client-side buffers around.					
			final int buffers[] = new int[1];
			glGenBuffers(1, buffers, 0);

			glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
			glBufferData(GL_ARRAY_BUFFER, cubeBuffer.capacity() * BYTES_PER_FLOAT, cubeBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);

			cubeBufferIdx = buffers[0];
			
			cubeBuffer.limit(0);
			cubeBuffer = null;
		}

		@Override
		public void render() {	    
			final int stride = (POSITION_DATA_SIZE + NORMAL_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE) * BYTES_PER_FLOAT;
			
			// Pass in the position information
			glBindBuffer(GL_ARRAY_BUFFER, cubeBufferIdx);
			glEnableVertexAttribArray(positionHandle);
			glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GL_FLOAT, false, stride, 0);

			// Pass in the normal information
			glBindBuffer(GL_ARRAY_BUFFER, cubeBufferIdx);
			glEnableVertexAttribArray(normalHandle);
			glVertexAttribPointer(normalHandle, NORMAL_DATA_SIZE, GL_FLOAT, false, stride, POSITION_DATA_SIZE * BYTES_PER_FLOAT);
			
			// Pass in the texture information
			glBindBuffer(GL_ARRAY_BUFFER, cubeBufferIdx);
			glEnableVertexAttribArray(textureCoordinateHandle);
			glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, GL_FLOAT, false, stride, (POSITION_DATA_SIZE + NORMAL_DATA_SIZE) * BYTES_PER_FLOAT);

			// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			// Draw the cubes.
			glDrawArrays(GL_TRIANGLES, 0, actualCubeFactor * actualCubeFactor * actualCubeFactor * 36);
		}

		@Override
		public void release() {
			// Delete buffers from OpenGL's memory
			final int[] buffersToDelete = new int[] {cubeBufferIdx};
			glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
		}
	}
}
