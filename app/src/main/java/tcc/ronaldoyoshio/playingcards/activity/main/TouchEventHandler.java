package tcc.ronaldoyoshio.playingcards.activity.main;

import android.view.MotionEvent;

import java.util.HashMap;

/**
 * Classe trata de toque na tela.
 * Created by mori on 28/07/16.
 */
public class TouchEventHandler {
    private HashMap<Integer, Float> mPreviousX = new HashMap<>();
    private HashMap<Integer, Float> mPreviousY = new HashMap<>();

    public boolean onTouchEvent(MotionEvent event, int width, int height) {
        boolean b = false;
        float x, y;
        int pointerId, index;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                for (index = 0; index < event.getPointerCount(); index++) {

                    // x, y é a posição do dedo em coordenada de pixel
                    x = event.getX(index);
                    y = event.getY(index);

                    pointerId = event.getPointerId(index);

                    float dx = mPreviousX.containsKey(pointerId) ?
                            x - mPreviousX.get(pointerId) : 0;
                    float dy = mPreviousY.containsKey(pointerId) ?
                            y - mPreviousY.get(pointerId) : 0;

                    b = onMove(pointerId, x, y, dx, dy, width, height);

                    mPreviousX.put(pointerId, x);
                    mPreviousY.put(pointerId, y);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                index = event.getActionIndex();

                // x, y é a posição do dedo em coordenada de pixel
                x = event.getX(index);
                y = event.getY(index);

                pointerId = event.getPointerId(index);
                b = onDown(pointerId, x, y, width, height);
                mPreviousX.put(pointerId, x);
                mPreviousY.put(pointerId, y);
                break;
            case MotionEvent.ACTION_UP:
                index = event.getActionIndex();
                pointerId = event.getPointerId(index);
                b = onUp();
                mPreviousX.remove(pointerId);
                mPreviousY.remove(pointerId);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                index = event.getActionIndex();

                // x, y é a posição do dedo em coordenada de pixel
                x = event.getX(index);
                y = event.getY(index);

                pointerId = event.getPointerId(index);
                b = onPointerDown(pointerId, x, y, width, height);
                mPreviousX.put(pointerId, x);
                mPreviousY.put(pointerId, y);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                index = event.getActionIndex();
                pointerId = event.getPointerId(index);
                b = onPointerUp(pointerId);
                mPreviousX.remove(pointerId);
                mPreviousY.remove(pointerId);
                break;
        }
        return b;
    }

    public boolean onPointerUp(int pointerId) {
        return false;
    }

    public boolean onPointerDown(int pointerId, float x, float y, int width, int height) {
        return false;
    }

    public boolean onUp() {
        return false;
    }

    public boolean onDown(int pointerId, float x, float y, int width, int height) {
        return false;
    }

    public boolean onMove(int pointerId, float x, float y, float dx, float dy, int width, int height) {
        return false;
    }
}
