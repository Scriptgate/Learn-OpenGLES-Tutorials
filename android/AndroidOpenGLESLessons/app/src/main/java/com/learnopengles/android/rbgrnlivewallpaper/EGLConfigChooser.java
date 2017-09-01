package com.learnopengles.android.rbgrnlivewallpaper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by willems on 1/09/2017.
 */
interface EGLConfigChooser {
	EGLConfig chooseConfig(EGL10 egl, EGLDisplay display);
}
