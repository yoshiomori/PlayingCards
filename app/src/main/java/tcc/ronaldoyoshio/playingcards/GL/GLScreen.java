package tcc.ronaldoyoshio.playingcards.GL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLScreen extends GLSurfaceView {
    private final GLRenderer renderer;
    private GLImage[] images;
    private float mPreviousX;
    private float mPreviousY;

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
            image.setResources(getResources());
        }

        renderer.setImages(images);
        this.images = images;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = (2 * event.getX() - getWidth()) / getWidth();
        float y = (2 * event.getY() - getHeight()) / getHeight();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                for (GLImage image :
                        images) {
                    image.onMove(dx, dy);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                for (GLImage image :
                        images) {
                    image.onDown(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                for (GLImage image :
                        images) {
                    image.onUp();
                }
                break;
        }

        mPreviousX = x;
        mPreviousY = y;
        requestRender();
        return true;
    }
}
