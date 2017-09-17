package com.learnopengles.android.lesson12;

import android.content.Context;

import com.learnopengles.android.R;

import net.scriptgate.android.common.Point3D;
import net.scriptgate.android.opengles.face.Point3DFace;
import net.scriptgate.android.opengles.matrix.ModelMatrix;
import net.scriptgate.android.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ProjectionMatrix;
import net.scriptgate.android.opengles.matrix.ViewMatrix;
import net.scriptgate.android.opengles.program.Program;
import net.scriptgate.android.opengles.renderer.RendererBase;

import java8.util.function.Consumer;

import static android.opengl.GLES20.*;
import static com.learnopengles.android.lesson12.Square.ELEMENTS_PER_FACE;
import static com.learnopengles.android.lesson12.Square.createSquare;
import static com.learnopengles.android.lesson12.SquareDataFactory.generateTextureData;
import static net.scriptgate.android.common.Color.*;

import static net.scriptgate.android.opengles.matrix.ViewMatrix.createViewBehindOrigin;
import static net.scriptgate.android.opengles.program.AttributeVariable.*;
import static net.scriptgate.android.opengles.program.ProgramBuilder.program;
import static net.scriptgate.android.opengles.texture.TextureHelper.loadTexture;

public class DankMemesRenderer extends RendererBase {

    private ModelMatrix modelMatrix = new ModelMatrix();
    private ViewMatrix viewMatrix;
    private ModelViewProjectionMatrix mvpMatrix = new ModelViewProjectionMatrix();

    private Program program;

    private Background background;
    private Grid grid;
    private Square horizon;
    private Square delorean;

    private Context activityContext;

    private float[] deltaRotationVector = new float[4];

    public DankMemesRenderer(Context activityContext) {
        super(ProjectionMatrix.createProjectionMatrix(150, 1));
        this.activityContext = activityContext;

        viewMatrix = createViewBehindOrigin();

        background = new Background();
        grid = new Grid();
        horizon = createHorizon();
        delorean = createDelorean();
    }

    private Square createHorizon() {
        float width = 1.0f;
        float height = 0.75f;

        Point3DFace face = new Point3DFace(
                new Point3D(0, 0, 0),
                new Point3D(width, 0, 0),
                new Point3D(0, height, 0),
                new Point3D(width, height, 0)
        );
        float[] verticesData = new float[ELEMENTS_PER_FACE * face.getNumberOfElements()];
        face.addFaceToArray(verticesData, 0);

        Square horizon = createSquare(
                new Point3D(-1.5f, 0.6f, 0),
                new Point3D(),
                verticesData,
                generateTextureData(3.0f, 1.0f));
        horizon.setScale(new Point3D(3, 1, 1));
        return horizon;

    }

    private Square createDelorean() {
        float width = 1.0f;
        float height = 0.7f;

        Point3DFace face = new Point3DFace(
                new Point3D(0, 0, 0),
                new Point3D(width, 0, 0),
                new Point3D(0, height, 0),
                new Point3D(width, height, 0)
        );
        float[] verticesData = new float[ELEMENTS_PER_FACE * face.getNumberOfElements()];
        face.addFaceToArray(verticesData, 0);

        Square delorean = createSquare(
                new Point3D(-0.75f, 1, 0.0f),
                new Point3D(180.0f, 0.0f, 0.0f),
                verticesData,
                generateTextureData(1.0f, 1.0f));
        delorean.setScale(new Point3D(1.5f, 1.5f, 1));
        return delorean;
    }

    @Override
    public void onSurfaceCreated() {
        glClearColor(GREY.red(), GREY.green(), GREY.blue(), GREY.alpha());

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        viewMatrix.onSurfaceCreated();
        viewMatrix.translate(new Point3D(0, -1, 0));

        background.setTexture(loadTexture(activityContext, R.drawable.background));
        grid.setTexture(loadTexture(activityContext, R.drawable.grid));
        horizon.setTexture(loadTexture(activityContext, R.drawable.horizon));
        delorean.setTexture(loadTexture(activityContext, R.drawable.delorean));
        glGenerateMipmap(GL_TEXTURE_2D);

        program = program()
                .withVertexShader(activityContext, R.raw.per_pixel_vertex_shader_texture)
                .withFragmentShader(activityContext, R.raw.per_pixel_fragment_shader_texture)
                .withAttributes(POSITION, COLOR)
                .build();

        program.useForRendering();
    }

    @Override
    public void onDrawFrame() {
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        program.useForRendering();

        background.update();
        grid.update();

        Consumer<Square> draw = new Consumer<Square>() {
            @Override
            public void accept(Square square) {
                square.draw(program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix);
            }
        };
        background.render(draw);
        grid.render(draw);
        draw.accept(horizon);
        delorean.transform(deltaRotationVector);
        draw.accept(delorean);
    }

    public void setGyroscopeValues(float[] deltaRotationVector) {
        this.deltaRotationVector = deltaRotationVector;
    }
}
