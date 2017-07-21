package com.learnopengles.android.lesson2;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.AbstractActivity;

public class LessonTwoActivity extends AbstractActivity {

	@Override
	public Renderer getRenderer() {
		return new LightingRenderer();
	}
}