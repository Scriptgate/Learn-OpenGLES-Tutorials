package net.scriptgate.opengles.renderer;

public interface Renderer {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();
}
