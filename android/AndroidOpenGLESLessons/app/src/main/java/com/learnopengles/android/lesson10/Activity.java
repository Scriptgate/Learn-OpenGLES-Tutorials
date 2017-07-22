package com.learnopengles.android.lesson10;

import com.learnopengles.android.activity.ActivityBase;
import com.learnopengles.android.renderer.Renderer;

public class Activity extends ActivityBase {
    @Override
    public Renderer getRenderer() {
        return new AdvancedTexturingRenderer(this);
    }
}