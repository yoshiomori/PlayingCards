package tcc.ronaldoyoshio.playingcards.GL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import java.util.HashMap;

public class GLScreen extends GLSurfaceView {
    private final GLRenderer renderer;
    private GLImage[] images;
    private HashMap<Integer, Float> mPreviousRX = new HashMap<>();
    private HashMap<Integer, Float> mPreviousRY = new HashMap<>();
    private long mPreviousDownTime = Long.MIN_VALUE;
    private HashMap<Integer, Float> mPreviousX = new HashMap<>();
    private HashMap<Integer, Float> mPreviousY = new HashMap<>();
    private int mainPointerId;

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
        boolean b = super.onTouchEvent(event);

        for (int index = 0; index < event.getPointerCount(); index++) {
            // x, y é a posição do dedo em coordenada de pixel
            float x = event.getX(index);
            float y = event.getY(index);

            // rX, rY é a posição do dedo nas coordenadas da tela
            float rX = (2 * x - getWidth()) / getWidth();
            float rY = (getHeight() - 2 * y) / getHeight();

            int pointerId = event.getPointerId(index);

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    // rDx, rDy é a variação da posição do dedo nas coordenadas da tela
                    float rDx = mPreviousRX.containsKey(pointerId) ?
                            rX - mPreviousRX.get(pointerId) : 0;
                    float rDy = mPreviousY.containsKey(pointerId) ?
                            rY - mPreviousRY.get(pointerId) : 0;

                    for (GLImage image :
                            images) {
                        b |= image.onMove(pointerId, rDx, rDy);
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    mainPointerId = pointerId;
                    long downTime = event.getDownTime();
                    long dt = downTime - mPreviousDownTime;
                    mPreviousDownTime = downTime;
                    for (GLImage image :
                            images) {
                        float dx = mPreviousX.containsKey(mainPointerId) ?
                                x - mPreviousX.get(mainPointerId) : Float.POSITIVE_INFINITY;
                        float dy = mPreviousY.containsKey(mainPointerId) ?
                                y - mPreviousY.get(mainPointerId) : Float.POSITIVE_INFINITY;
                        if (isDoubleTap(dt, dx, dy)) {
                            b |= image.onDoubleTap();
                        }
                        b |= image.onDown(rX, rY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    for (GLImage image :
                            images) {
                        b |= image.onUp();
                    }
                    break;
            }

            if (mainPointerId != pointerId) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        for (GLImage image :
                                images) {
                            System.out.println(pointerId);
                            b |= image.onPointerDown(pointerId, rX, rY);
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        for (GLImage image :
                                images) {
                            b |= image.onPointerUp(pointerId);
                        }
                        break;
                }
            }

            mPreviousRX.put(pointerId, rX);
            mPreviousRY.put(pointerId, rY);
            mPreviousX.put(pointerId, x);
            mPreviousY.put(pointerId, y);
            if (b) {
                requestRender();
            }
        }
        return b;
    }


    private boolean isDoubleTap(long dt, float dx, float dy) {
        return dx * dx + dy * dy <= 1600 && dt * dt <= 100000;
    }
}
