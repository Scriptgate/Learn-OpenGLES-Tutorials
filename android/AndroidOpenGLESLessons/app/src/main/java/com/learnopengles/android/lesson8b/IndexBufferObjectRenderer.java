package com.learnopengles.android.lesson8b;

import android.content.Context;

import com.learnopengles.android.R;
import com.learnopengles.android.common.Point3D;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.lesson9.IsometricProjectionMatrix;
import com.learnopengles.android.program.Program;
import com.learnopengles.android.renderer.RendererBase;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.common.TextureHelper.loadTexture;
import static com.learnopengles.android.program.AttributeVariable.*;
import static com.learnopengles.android.program.UniformVariable.*;
import static java.util.Arrays.asList;

class IndexBufferObjectRenderer extends RendererBase {

	private final ModelMatrix modelMatrix = new ModelMatrix();
	private final ViewMatrix viewMatrix;
	private final ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

	private Program program;

	private IndexBufferObjects ibo;

    private Context activityContext;

    IndexBufferObjectRenderer(Context context) {
        super(new IsometricProjectionMatrix(100.0f));
        this.activityContext = context;

        float dist = 5;

        Point3D eye = new Point3D(dist, dist, dist);
        Point3D look = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        viewMatrix = new ViewMatrix(eye, look, up);
    }

    @Override
	public void onSurfaceCreated() {
        int textureHandle = loadTexture(activityContext, R.drawable.colormap);
		ibo = new IndexBufferObjects(textureHandle);

		// Set the background clear color to black.
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

		viewMatrix.onSurfaceCreated();
        viewMatrix.translate(new Point3D(0, -2, 0));

        program = Program.createProgram("per_pixel_vertex_shader_texture", "per_pixel_fragment_shader_texture", asList(POSITION, TEXTURE_COORDINATE));
	}

	@Override
	public void onDrawFrame() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        program.useForRendering();

        modelMatrix.setIdentity();

        mvpMatrix.multiply(modelMatrix, viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        if(ibo != null) {
            ibo.render(program);
        }
	}
}
