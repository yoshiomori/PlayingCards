package tcc.ronaldoyoshio.playingcards.activity.main;

import android.view.MotionEvent;

import java.util.HashMap;

/**
 * Classe trata de toque na tela.
 * Created by mori on 28/07/16.
 */
public class EventHandler {
    private HashMap<Integer, Float> mPreviousRX = new HashMap<>();
    private HashMap<Integer, Float> mPreviousRY = new HashMap<>();
    private long mPreviousDownTime = Long.MIN_VALUE;
    private HashMap<Integer, Float> mPreviousX = new HashMap<>();
    private HashMap<Integer, Float> mPreviousY = new HashMap<>();
    private int mainPointerId;

    public boolean onTouchEvent(MotionEvent event, int width, int height) {
        boolean b = false;
        for (int index = 0; index < event.getPointerCount(); index++) {
            // x, y é a posição do dedo em coordenada de pixel
            float x = event.getX(index);
            float y = event.getY(index);

            // rX, rY é a posição do dedo nas coordenadas da tela
            float rX = (2 * x - width) / width;
            float rY = (height - 2 * y) / height;

            int pointerId = event.getPointerId(index);

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    // rDx, rDy é a variação da posição do dedo nas coordenadas da tela
                    float rDx = mPreviousRX.containsKey(pointerId) ?
                            rX - mPreviousRX.get(pointerId) : 0;
                    float rDy = mPreviousY.containsKey(pointerId) ?
                            rY - mPreviousRY.get(pointerId) : 0;

                    b |= onMove(pointerId, rDx, rDy);
                    break;
                case MotionEvent.ACTION_DOWN:
                    mainPointerId = pointerId;
                    long downTime = event.getDownTime();
                    long dt = downTime - mPreviousDownTime;
                    mPreviousDownTime = downTime;

                    float dx = mPreviousX.containsKey(mainPointerId) ?
                            x - mPreviousX.get(mainPointerId) : Float.POSITIVE_INFINITY;
                    float dy = mPreviousY.containsKey(mainPointerId) ?
                            y - mPreviousY.get(mainPointerId) : Float.POSITIVE_INFINITY;
                    if (isDoubleTap(dt, dx, dy)) {
                        b |= onDoubleTap();
                    }
                    b |= onDown(rX, rY);
                    break;
                case MotionEvent.ACTION_UP:
                    b |= onUp();
                    break;
            }

            if (mainPointerId != pointerId) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        b |= onPointerDown(pointerId, rX, rY);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        b |= onPointerUp(pointerId);
                        break;
                }
            }

            mPreviousRX.put(pointerId, rX);
            mPreviousRY.put(pointerId, rY);
            mPreviousX.put(pointerId, x);
            mPreviousY.put(pointerId, y);
        }
        return b;
    }

    public boolean onPointerUp(int pointerId) {
        return false;
    }

    public boolean onPointerDown(int pointerId, float rX, float rY) {
        return false;
    }

    public boolean onUp() {
        return false;
    }

    public boolean onDown(float rX, float rY) {
        return false;
    }

    public boolean onDoubleTap() {
        return false;
    }

    public boolean onMove(int pointerId, float rDx, float rDy) {
        return false;
    }

    public boolean isDoubleTap(long dt, float dx, float dy) {
        return dx * dx + dy * dy <= 1600 && dt * dt <= 100000;
    }
}
