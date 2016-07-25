package tcc.ronaldoyoshio.playingcards.GL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLScreen extends GLSurfaceView {
    private final GLRenderer renderer;
    private GLImage[] images;

    public GLScreen(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        renderer = new GLRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setImages(GLImage... images) {
        for (GLImage image :
                images) {
            image.setContext(this);
        }
        renderer.setImages(images);
        this.images = images;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (GLImage image :
                images) {
            image.onTouchEvent(event);
        }
        requestRender();
        return true;
    }
}
