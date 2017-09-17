package com.learnopengles.android.livewallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;

import com.learnopengles.android.lesson12.*;

import static net.scriptgate.android.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToGLSurfaceViewRenderer;

public class LessonTwelveWallpaperService extends GLWallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new OpenGLES2Engine();
    }

    private class OpenGLES2Engine extends GLEngine {

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            DankMemesRenderer renderer = new DankMemesRenderer(LessonTwelveWallpaperService.this);

            if (supportsOpenGLES20()) {
                setEGLContextClientVersion(2);
                setPreserveEGLContextOnPause(true);
                setRenderer(adaptToGLSurfaceViewRenderer(renderer));
            } else {
                throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
            }

            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            addComponent(new SensorService(sensorManager, renderer));
        }

        private boolean supportsOpenGLES20() {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            return activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000;
        }
    }
}
