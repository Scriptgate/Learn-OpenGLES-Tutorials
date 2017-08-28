package com.learnopengles.android.livewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.lesson12.DankMemesRenderer;

import static net.scriptgate.opengles.renderer.RendererAdapter.adaptToGLSurfaceViewRenderer;

public class LessonTwelveWallpaperService extends OpenGLES2WallpaperService {
	@Override
	Renderer getNewRenderer() {
		return adaptToGLSurfaceViewRenderer(new DankMemesRenderer(this));
	}
}
