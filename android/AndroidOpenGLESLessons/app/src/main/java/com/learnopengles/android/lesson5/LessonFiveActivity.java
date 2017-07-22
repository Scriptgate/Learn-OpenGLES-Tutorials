package com.learnopengles.android.lesson5;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.learnopengles.android.R;

public class LessonFiveActivity extends Activity {

    private LessonFiveGLSurfaceView glSurfaceView;

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

    private boolean supportsOpenGLES20() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOWED_TOAST, true);
    }
}