package com.learnopengles.android.livewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.lesson3.PerPixelLightingRenderer;

import static com.learnopengles.android.renderer.RendererAdapter.adaptToGLSurfaceViewRenderer;

public class LessonThreeWallpaperService extends OpenGLES2WallpaperService {
	@Override
	Renderer getNewRenderer() {
		return adaptToGLSurfaceViewRenderer(new PerPixelLightingRenderer());
	}
}
