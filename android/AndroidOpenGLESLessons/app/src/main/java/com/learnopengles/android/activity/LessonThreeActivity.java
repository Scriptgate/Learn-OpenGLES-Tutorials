package com.learnopengles.android.activity;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.lesson3.LessonThreeRenderer;

public class LessonThreeActivity extends AbstractActivity {

	@Override
	GLSurfaceView.Renderer getRenderer() {
		return new LessonThreeRenderer();
	}
}