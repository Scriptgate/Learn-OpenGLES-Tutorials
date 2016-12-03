package com.learnopengles.android.switchinglivewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.lesson3.PerPixelLightingRenderer;

public class LessonThreeWallpaperService extends OpenGLES2WallpaperService {
	@Override
	Renderer getNewRenderer() {
		return new PerPixelLightingRenderer();
	}
}
