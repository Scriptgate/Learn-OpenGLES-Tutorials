package com.learnopengles.android.lesson8b;

import android.opengl.GLSurfaceView.Renderer;

import com.learnopengles.android.activity.ActivityBase;

public class Activity extends ActivityBase {

	@Override
	public Renderer getRenderer() {
		return new IndexBufferObjectRenderer(this);
	}
}