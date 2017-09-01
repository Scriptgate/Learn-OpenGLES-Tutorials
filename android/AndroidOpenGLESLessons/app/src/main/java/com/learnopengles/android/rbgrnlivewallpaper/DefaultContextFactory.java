package com.learnopengles.android.rbgrnlivewallpaper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by willems on 1/09/2017.
 */
class DefaultContextFactory implements EGLContextFactory {
	private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

	public EGLContext createContext(EGL10 egl, EGLDisplay display,
                                    EGLConfig config, int eglContextClientVersion) {
		int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION,
				eglContextClientVersion, EGL10.EGL_NONE };

		return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT,
				eglContextClientVersion != 0 ? attrib_list : null);
	}

	public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
		egl.eglDestroyContext(display, context);
	}
}
