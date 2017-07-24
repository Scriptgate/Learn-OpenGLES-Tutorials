package net.scriptgate.opengles.light.renderer;

import net.scriptgate.opengles.light.Light;
import net.scriptgate.opengles.matrix.ModelViewProjectionMatrix;
import net.scriptgate.opengles.matrix.ProjectionMatrix;
import net.scriptgate.opengles.matrix.ViewMatrix;
import net.scriptgate.opengles.program.Program;

import static android.opengl.GLES20.*;
import static java.util.Collections.singletonList;
import static net.scriptgate.opengles.program.AttributeVariable.POSITION;
import static net.scriptgate.opengles.program.Program.createProgram;
import static net.scriptgate.opengles.program.UniformVariable.MVP_MATRIX;

public class LightRenderer {
    private final Program program;
    private final ViewMatrix viewMatrix;
    private final ProjectionMatrix projectionMatrix;
    private final ModelViewProjectionMatrix mvpMatrix;

    private LightRenderer(Program program, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix, ModelViewProjectionMatrix mvpMatrix) {
        this.program = program;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mvpMatrix = mvpMatrix;
    }

    public void draw(Light light) {
        int positionHandle = program.getHandle(POSITION);
        glDisableVertexAttribArray(positionHandle);
        glVertexAttrib3fv(positionHandle, light.getPositionInModelSpace(), 0);

        mvpMatrix.multiply(light.getModelMatrix(), viewMatrix, projectionMatrix);
        mvpMatrix.passTo(program.getHandle(MVP_MATRIX));

        glDrawArrays(GL_POINTS, 0, 1);
    }

    public void useForRendering() {
        program.useForRendering();
    }

    public static LightRenderer createLightRenderer(ModelViewProjectionMatrix mvpMatrix, ViewMatrix viewMatrix, ProjectionMatrix projectionMatrix) {
        Program pointProgram = createProgram("point_vertex_shader", "point_fragment_shader", singletonList(POSITION));
        return new LightRenderer(pointProgram, viewMatrix, projectionMatrix, mvpMatrix);
    }
}
