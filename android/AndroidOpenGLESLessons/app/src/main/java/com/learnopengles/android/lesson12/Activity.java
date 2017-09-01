package com.learnopengles.android.lesson12;

import net.scriptgate.opengles.activity.ActivityBase;
import net.scriptgate.opengles.renderer.Renderer;

public class Activity extends ActivityBase {

    @Override
    public Renderer getRenderer() {
        return new DankMemesRenderer(this);
    }
}