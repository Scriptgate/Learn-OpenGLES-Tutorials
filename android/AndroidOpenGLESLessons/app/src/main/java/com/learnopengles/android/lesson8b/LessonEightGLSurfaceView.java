package com.learnopengles.android.lesson8b;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.widget.Toast;

import com.learnopengles.android.R;

public class LessonEightGLSurfaceView extends GLSurfaceView implements ErrorHandler {

    public LessonEightGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void handleError(final ErrorType errorType, final String cause) {
        // Queue on UI thread.
        post(new Runnable() {
            @Override
            public void run() {
                final String text;

                switch (errorType) {
                    case BUFFER_CREATION_ERROR:
                        text = String.format(getContext().getResources().getString(R.string.lesson_eight_error_could_not_create_vbo), cause);
                        break;
                    default:
                        text = String.format(getContext().getResources().getString(R.string.lesson_eight_error_unknown), cause);
                }
                Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
