package net.scriptgate.opengles.renderer;

import net.scriptgate.opengles.matrix.ProjectionMatrix;

public abstract class RendererBase implements Renderer {

    protected ProjectionMatrix projectionMatrix;

    protected RendererBase() {
        this(ProjectionMatrix.createProjectionMatrix());
    }

    protected RendererBase(ProjectionMatrix projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    @Override
    public final void onSurfaceChanged(int width, int height) {
        projectionMatrix.onSurfaceChanged(width, height);
    }

}
