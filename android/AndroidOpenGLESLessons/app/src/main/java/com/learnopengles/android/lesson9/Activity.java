package com.learnopengles.android.lesson9;

import net.scriptgate.android.opengles.activity.ActivityBase;
import net.scriptgate.android.opengles.renderer.Renderer;

public class Activity extends ActivityBase {
    @Override
    public Renderer getRenderer() {
        return new CameraRenderer(this);
    }
}
