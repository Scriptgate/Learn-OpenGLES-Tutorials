package com.learnopengles.android.activity;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.lesson2.LightingRenderer;

public class LessonTwoActivity extends AbstractActivity {

	@Override
	GLSurfaceView.Renderer getRenderer() {
		return new LightingRenderer();
	}
}