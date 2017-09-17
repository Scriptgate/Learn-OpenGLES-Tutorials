package com.learnopengles.android.rbgrnlivewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.lesson3.PerPixelLightingRenderer;

import static net.scriptgate.android.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToGLSurfaceViewRenderer;

public class LessonThreeWallpaperService extends OpenGLES2WallpaperService {
	@Override
	Renderer getNewRenderer() {
		return adaptToGLSurfaceViewRenderer(new PerPixelLightingRenderer());
	}
}
