package com.learnopengles.android.lesson3;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.AbstractActivity;

public class LessonThreeActivity extends AbstractActivity {

	@Override
	public Renderer getRenderer() {
		return new PerPixelLightingRenderer();
	}
}