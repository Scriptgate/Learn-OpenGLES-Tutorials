package com.learnopengles.android.livewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.lesson3.PerPixelLightingRenderer;

public class LessonThreeWallpaperService extends OpenGLES2WallpaperService {
	@Override
	Renderer getNewRenderer() {
		return new PerPixelLightingRenderer();
	}
}
