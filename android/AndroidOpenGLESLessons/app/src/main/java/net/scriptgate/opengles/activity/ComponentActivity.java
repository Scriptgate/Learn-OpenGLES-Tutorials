package net.scriptgate.opengles.activity;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;

import java8.util.function.Consumer;

import static java8.util.stream.StreamSupport.stream;

public class ComponentActivity extends Activity {

    Collection<Resumable> components;

    public ComponentActivity() {
        this.components = new ArrayList<>();
    }

    public void addComponent(Resumable resumable) {
        components.add(resumable);
    }

    protected boolean supportsOpenGLES20() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000;
    }

    @Override
    protected final void onResume() {
        super.onResume();
        stream(components).forEach(new Consumer<Resumable>() {
            @Override
            public void accept(Resumable resumable) {
                resumable.onResume();
            }
        });
    }

    @Override
    protected final void onPause() {
        super.onPause();
        stream(components).forEach(new Consumer<Resumable>() {
            @Override
            public void accept(Resumable resumable) {
                resumable.onPause();
            }
        });
    }

}
