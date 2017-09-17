package com.learnopengles.android.lesson8b;

import net.scriptgate.android.opengles.activity.ActivityBase;
import net.scriptgate.android.opengles.renderer.Renderer;

public class Activity extends ActivityBase {

	@Override
	public Renderer getRenderer() {
		return new IndexBufferObjectRenderer(this);
	}
}