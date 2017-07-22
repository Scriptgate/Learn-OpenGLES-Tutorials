package com.learnopengles.android.lesson8b;

import com.learnopengles.android.activity.ActivityBase;
import com.learnopengles.android.renderer.Renderer;

public class Activity extends ActivityBase {

	@Override
	public Renderer getRenderer() {
		return new IndexBufferObjectRenderer(this);
	}
}