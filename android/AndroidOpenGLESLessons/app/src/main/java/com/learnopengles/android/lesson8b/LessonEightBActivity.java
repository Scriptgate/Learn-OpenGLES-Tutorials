package com.learnopengles.android.lesson8b;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.AbstractActivity;

public class LessonEightBActivity extends AbstractActivity {

	@Override
	public Renderer getRenderer() {
		return new IndexBufferObjectRenderer(this);
	}
}