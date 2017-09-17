package com.learnopengles.android.lesson10;

import net.scriptgate.android.opengles.activity.ActivityBase;
import net.scriptgate.android.opengles.renderer.Renderer;

public class Activity extends ActivityBase {
    @Override
    public Renderer getRenderer() {
        return new AdvancedTexturingRenderer(this);
    }
}