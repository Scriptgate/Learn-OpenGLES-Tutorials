package com.learnopengles.android.lesson6;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;

import com.learnopengles.android.R;

import net.scriptgate.opengles.activity.ComponentActivity;

import static net.scriptgate.opengles.activity.adapter.GLSurfaceViewAdapter.adaptToResumable;

public class Activity extends ComponentActivity {

    private TextureFilteringRenderer renderer;
    private LessonSixGLSurfaceView surfaceView;

    private static final int MIN_DIALOG = 1;
    private static final int MAG_DIALOG = 2;

    private static final String MIN_SETTING = "min_setting";
    private static final String MAG_SETTING = "mag_setting";

    private int minSetting = -1;
    private int magSetting = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lesson_six);

        surfaceView = (LessonSixGLSurfaceView) findViewById(R.id.gl_surface_view);


        if (supportsOpenGLES20()) {

            surfaceView.setEGLContextClientVersion(2);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            // Set the renderer to our demo renderer, defined below.
            renderer = new TextureFilteringRenderer(this);
            surfaceView.setRenderer(renderer, displayMetrics.density);
            addComponent(adaptToResumable(surfaceView));
        } else {
            throw new UnsupportedOperationException("This activity requires OpenGL ES 2.0");
        }

        findViewById(R.id.button_set_min_filter).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(MIN_DIALOG);
            }
        });

        findViewById(R.id.button_set_mag_filter).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(MAG_DIALOG);
            }
        });

        // Restore previous settings
        if (savedInstanceState != null) {
            minSetting = savedInstanceState.getInt(MIN_SETTING, -1);
            magSetting = savedInstanceState.getInt(MAG_SETTING, -1);

            if (minSetting != -1) {
                setMinSetting(minSetting);
            }
            if (magSetting != -1) {
                setMagSetting(magSetting);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MIN_SETTING, minSetting);
        outState.putInt(MAG_SETTING, magSetting);
    }

    private void setMinSetting(final int item) {
        minSetting = item;

        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                final int filter;

                switch (item) {
                    case 0:
                        filter = GLES20.GL_NEAREST;
                        break;
                    case 1:
                        filter = GLES20.GL_LINEAR;
                        break;
                    case 2:
                        filter = GLES20.GL_NEAREST_MIPMAP_NEAREST;
                        break;
                    case 3:
                        filter = GLES20.GL_NEAREST_MIPMAP_LINEAR;
                        break;
                    case 4:
                        filter = GLES20.GL_LINEAR_MIPMAP_NEAREST;
                        break;
                    case 5:
                    default:
                        filter = GLES20.GL_LINEAR_MIPMAP_LINEAR;
                        break;
                }

                renderer.setMinFilter(filter);
            }
        });
    }

    private void setMagSetting(final int item) {
        magSetting = item;

        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                final int filter;

                switch (item) {
                    case 0:
                        filter = GLES20.GL_NEAREST;
                        break;
                    case 1:
                    default:
                        filter = GLES20.GL_LINEAR;
                        break;
                }

                renderer.setMagFilter(filter);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;

        switch (id) {
            case MIN_DIALOG: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getText(R.string.lesson_six_set_min_filter_message));
                builder.setItems(getResources().getStringArray(R.array.lesson_six_min_filter_types), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        setMinSetting(item);
                    }
                });

                dialog = builder.create();
            }
            break;
            case MAG_DIALOG: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getText(R.string.lesson_six_set_mag_filter_message));
                builder.setItems(getResources().getStringArray(R.array.lesson_six_mag_filter_types), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        setMagSetting(item);
                    }
                });

                dialog = builder.create();
            }
            break;
            default:
                dialog = null;
        }

        return dialog;
    }
}