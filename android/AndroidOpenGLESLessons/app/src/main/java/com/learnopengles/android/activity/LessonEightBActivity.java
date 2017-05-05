package com.learnopengles.android.activity;

import android.opengl.GLSurfaceView;

import com.learnopengles.android.lesson8b.IndexBufferObjectRenderer;

public class LessonEightBActivity extends AbstractActivity {

	@Override
	GLSurfaceView.Renderer getRenderer() {
		return new IndexBufferObjectRenderer();
	}
}