package com.learnopengles.android.switchinglivewallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.learnopengles.android.util.LoggerConfig;
import com.learnopengles.android.switchinglivewallpaper.BaseConfigChooser.ComponentSizeChooser;
import com.learnopengles.android.switchinglivewallpaper.BaseConfigChooser.SimpleEGLConfigChooser;

public abstract class GLWallpaperService extends WallpaperService {
	private static final String TAG = "GLWallpaperService";
	
	interface OpenGLEngine {
		void setEGLContextClientVersion(int version);
		
		 void setRenderer(Renderer renderer);
	}
	
	public class GLSurfaceViewEngine extends Engine implements OpenGLEngine {
		class WallpaperGLSurfaceView extends GLSurfaceView {
			private static final String TAG = "WallpaperGLSurfaceView";

			WallpaperGLSurfaceView(Context context) {
				super(context);

				if (LoggerConfig.ON) {
					Log.d(TAG, "WallpaperGLSurfaceView(" + context + ")");
				}
			}

			@Override
			public SurfaceHolder getHolder() {
				if (LoggerConfig.ON) {
					Log.d(TAG, "getHolder(): returning " + getSurfaceHolder());
				}

				return getSurfaceHolder();
			}

			public void onDestroy() {
				if (LoggerConfig.ON) {
					Log.d(TAG, "onDestroy()");
				}

				super.onDetachedFromWindow();
			}
		}

		private static final String TAG = "GLSurfaceViewEngine";

		private WallpaperGLSurfaceView glSurfaceView;
		private boolean rendererHasBeenSet;		

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			if (LoggerConfig.ON) {
				Log.d(TAG, "onCreate(" + surfaceHolder + ")");
			}

			super.onCreate(surfaceHolder);

			glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			if (LoggerConfig.ON) {
				Log.d(TAG, "onVisibilityChanged(" + visible + ")");
			}

			super.onVisibilityChanged(visible);

			if (rendererHasBeenSet) {
				if (visible) {
					glSurfaceView.onResume();
				} else {					
					glSurfaceView.onPause();				
				}
			}
		}		

		@Override
		public void onDestroy() {
			if (LoggerConfig.ON) {
				Log.d(TAG, "onDestroy()");
			}

			super.onDestroy();
			glSurfaceView.onDestroy();
		}
		
		public void setRenderer(Renderer renderer) {
			if (LoggerConfig.ON) {
				Log.d(TAG, "setRenderer(" + renderer + ")");
			}

			glSurfaceView.setRenderer(renderer);
			rendererHasBeenSet = true;
		}

		public void setEGLContextClientVersion(int version) {
			if (LoggerConfig.ON) {
				Log.d(TAG, "setEGLContextClientVersion(" + version + ")");
			}

			glSurfaceView.setEGLContextClientVersion(version);
		}
	}
	
	public class RgbrnGLEngine extends Engine implements OpenGLEngine {
		public final static int RENDERMODE_WHEN_DIRTY = 0;
		public final static int RENDERMODE_CONTINUOUSLY = 1;

		private GLThread mGLThread;
		private EGLConfigChooser mEGLConfigChooser;
		private EGLContextFactory mEGLContextFactory;
		private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
		private GLWrapper mGLWrapper;
		private int mDebugFlags;
		private int mEGLContextClientVersion;

		public RgbrnGLEngine() {
			super();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			if (visible) {
				onResume();
			} else {
				onPause();
			}
			super.onVisibilityChanged(visible);
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			// Log.d(TAG, "GLEngine.onCreate()");
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			// Log.d(TAG, "GLEngine.onDestroy()");
			mGLThread.requestExitAndWait();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			// Log.d(TAG, "onSurfaceChanged()");
			mGLThread.onWindowResize(width, height);
			super.onSurfaceChanged(holder, format, width, height);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			Log.d(TAG, "onSurfaceCreated()");
			mGLThread.surfaceCreated(holder);
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG, "onSurfaceDestroyed()");
			mGLThread.surfaceDestroyed();
			super.onSurfaceDestroyed(holder);
		}

		/**
		 * An EGL helper class.
		 */
		public void setGLWrapper(GLWrapper glWrapper) {
			mGLWrapper = glWrapper;
		}

		public void setDebugFlags(int debugFlags) {
			mDebugFlags = debugFlags;
		}

		public int getDebugFlags() {
			return mDebugFlags;
		}

		public void setEGLContextClientVersion(int version) {
			checkRenderThreadState();
			mEGLContextClientVersion = version;
		}

		public void setRenderer(Renderer renderer) {
			checkRenderThreadState();
			if (mEGLConfigChooser == null) {
				mEGLConfigChooser = new SimpleEGLConfigChooser(true, mEGLContextClientVersion);
			}
			if (mEGLContextFactory == null) {
				mEGLContextFactory = new DefaultContextFactory();
			}
			if (mEGLWindowSurfaceFactory == null) {
				mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
			}
			mGLThread = new GLThread(renderer, mEGLConfigChooser,
					mEGLContextFactory, mEGLWindowSurfaceFactory, mGLWrapper,
					mEGLContextClientVersion);
			mGLThread.start();
		}

		public void setEGLContextFactory(EGLContextFactory factory) {
			checkRenderThreadState();
			mEGLContextFactory = factory;
		}

		public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
			checkRenderThreadState();
			mEGLWindowSurfaceFactory = factory;
		}

		public void setEGLConfigChooser(EGLConfigChooser configChooser) {
			checkRenderThreadState();
			mEGLConfigChooser = configChooser;
		}

		public void setEGLConfigChooser(boolean needDepth) {
			setEGLConfigChooser(new SimpleEGLConfigChooser(needDepth, mEGLContextClientVersion));
		}

		public void setEGLConfigChooser(int redSize, int greenSize,
				int blueSize, int alphaSize, int depthSize, int stencilSize) {
			setEGLConfigChooser(new ComponentSizeChooser(redSize, greenSize,
					blueSize, alphaSize, depthSize, stencilSize, mEGLContextClientVersion));
		}

		public void setRenderMode(int renderMode) {
			mGLThread.setRenderMode(renderMode);
		}

		public int getRenderMode() {
			return mGLThread.getRenderMode();
		}

		public void requestRender() {
			mGLThread.requestRender();
		}

		public void onPause() {
			mGLThread.onPause();
		}

		public void onResume() {
			mGLThread.onResume();
		}

		public void queueEvent(Runnable r) {
			mGLThread.queueEvent(r);
		}

		private void checkRenderThreadState() {
			if (mGLThread != null) {
				throw new IllegalStateException(
						"setRenderer has already been called for this instance.");
			}
		}
	}
}