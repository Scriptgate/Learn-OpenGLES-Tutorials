package com.learnopengles.android.lesson5;

import android.os.Bundle;
import android.widget.Toast;

import com.learnopengles.android.R;
import net.scriptgate.opengles.activity.ActivityWithViewBase;

public class Activity extends ActivityWithViewBase<LessonFiveGLSurfaceView> {

    private static final String SHOWED_TOAST = "showed_toast";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new LessonFiveGLSurfaceView(this);

        if (supportsOpenGLES20()) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new BlendingRenderer());
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        setContentView(glSurfaceView);

        if (shouldShowHelpMessage(savedInstanceState)) {
            Toast.makeText(this, R.string.lesson_five_startup_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean shouldShowHelpMessage(Bundle savedInstanceState) {
        return savedInstanceState == null || !savedInstanceState.getBoolean(SHOWED_TOAST, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOWED_TOAST, true);
    }
}