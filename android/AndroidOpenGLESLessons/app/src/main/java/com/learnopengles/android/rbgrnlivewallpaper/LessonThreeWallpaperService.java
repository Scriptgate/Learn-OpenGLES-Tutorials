package com.learnopengles.android.rbgrnlivewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.lesson3.PerPixelLightingRenderer;

import static net.scriptgate.opengles.renderer.RendererAdapter.adaptToGLSurfaceViewRenderer;

public class LessonThreeWallpaperService extends OpenGLES2WallpaperService {
	@Override
	Renderer getNewRenderer() {
		return adaptToGLSurfaceViewRenderer(new PerPixelLightingRenderer());
	}
}
