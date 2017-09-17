package com.learnopengles.android.lesson1;


import net.scriptgate.android.opengles.activity.ActivityBase;
import net.scriptgate.android.opengles.renderer.Renderer;

public class Activity extends ActivityBase {

    @Override
    public Renderer getRenderer() {
        return new BasicDrawingRenderer();
    }
}