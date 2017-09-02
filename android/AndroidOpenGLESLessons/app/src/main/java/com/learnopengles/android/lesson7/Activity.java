package com.learnopengles.android.lesson7;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.learnopengles.android.R;
import net.scriptgate.opengles.activity.ComponentActivity;

import static net.scriptgate.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToResumable;

public class Activity extends ComponentActivity {

    private VertexBufferObjectRenderer renderer;
    private LessonSevenGLSurfaceView surfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lesson_seven);

        surfaceView = (LessonSevenGLSurfaceView) findViewById(R.id.gl_surface_view);

        if (supportsOpenGLES20()) {
            surfaceView.setEGLContextClientVersion(2);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            renderer = new VertexBufferObjectRenderer(this, surfaceView);
            surfaceView.setRenderer(renderer, displayMetrics.density);
            addComponent(adaptToResumable(surfaceView));
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        findViewById(R.id.button_decrease_num_cubes).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseCubeCount();
            }
        });

        findViewById(R.id.button_increase_num_cubes).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseCubeCount();
            }
        });

        findViewById(R.id.button_switch_VBOs).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVBOs();
            }
        });

        findViewById(R.id.button_switch_stride).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStride();
            }
        });
    }

    private void decreaseCubeCount() {
        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.decreaseCubeCount();
            }
        });
    }

    private void increaseCubeCount() {
        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.increaseCubeCount();
            }
        });
    }

    private void toggleVBOs() {
        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.toggleVBOs();
            }
        });
    }

    protected void toggleStride() {
        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.toggleStride();
            }
        });
    }

    public void updateVboStatus(final boolean usingVbos) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (usingVbos) {
                    ((Button) findViewById(R.id.button_switch_VBOs)).setText(R.string.lesson_seven_using_VBOs);
                } else {
                    ((Button) findViewById(R.id.button_switch_VBOs)).setText(R.string.lesson_seven_not_using_VBOs);
                }
            }
        });
    }

    public void updateStrideStatus(final boolean useStride) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (useStride) {
                    ((Button) findViewById(R.id.button_switch_stride)).setText(R.string.lesson_seven_using_stride);
                } else {
                    ((Button) findViewById(R.id.button_switch_stride)).setText(R.string.lesson_seven_not_using_stride);
                }
            }
        });
    }
}