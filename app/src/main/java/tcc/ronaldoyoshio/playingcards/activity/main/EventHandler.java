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
        float x, y, rX, rY;
        int pointerId, index;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                for (index = 0; index < event.getPointerCount(); index++) {
                    // x, y é a posição do dedo em coordenada de pixel
                    x = event.getX(index);
                    y = event.getY(index);

                    // rX, rY é a posição do dedo nas coordenadas da tela
                    rX = (2 * x - width) / width;
                    rY = (height - 2 * y) / height;

                    pointerId = event.getPointerId(index);


                    // rDx, rDy é a variação da posição do dedo nas coordenadas da tela
                    float rDx = mPreviousRX.containsKey(pointerId) ?
                            rX - mPreviousRX.get(pointerId) : 0;
                    float rDy = mPreviousY.containsKey(pointerId) ?
                            rY - mPreviousRY.get(pointerId) : 0;

                    b = onMove(pointerId, rDx, rDy);

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
                break;
            case MotionEvent.ACTION_DOWN:
                index = event.getActionIndex();

                // x, y é a posição do dedo em coordenada de pixel
                x = event.getX(index);
                y = event.getY(index);

                // rX, rY é a posição do dedo nas coordenadas da tela
                rX = (2 * x - width) / width;
                rY = (height - 2 * y) / height;

                pointerId = event.getPointerId(index);

                mainPointerId = pointerId;
                long downTime = event.getDownTime();
                long dt = downTime - mPreviousDownTime;
                mPreviousDownTime = downTime;

                float dx = mPreviousX.containsKey(mainPointerId) ?
                        x - mPreviousX.get(mainPointerId) : Float.POSITIVE_INFINITY;
                float dy = mPreviousY.containsKey(mainPointerId) ?
                        y - mPreviousY.get(mainPointerId) : Float.POSITIVE_INFINITY;
                if (isDoubleTap(dt, dx, dy)) {
                    b = onDoubleTap();
                }
                b |= onDown(pointerId, rX, rY);

                mPreviousRX.put(pointerId, rX);
                mPreviousRY.put(pointerId, rY);
                mPreviousX.put(pointerId, x);
                mPreviousY.put(pointerId, y);
                break;
            case MotionEvent.ACTION_UP:
                // x, y é a posição do dedo em coordenada de pixel
                x = event.getX(0);
                y = event.getY(0);

                // rX, rY é a posição do dedo nas coordenadas da tela
                rX = (2 * x - width) / width;
                rY = (height - 2 * y) / height;

                pointerId = event.getPointerId(0);

                b = onUp();

                mPreviousRX.put(pointerId, rX);
                mPreviousRY.put(pointerId, rY);
                mPreviousX.put(pointerId, x);
                mPreviousY.put(pointerId, y);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                index = event.getActionIndex();
                System.out.println(index);
                // x, y é a posição do dedo em coordenada de pixel
                x = event.getX(index);
                y = event.getY(index);

                // rX, rY é a posição do dedo nas coordenadas da tela
                rX = (2 * x - width) / width;
                rY = (height - 2 * y) / height;

                pointerId = event.getPointerId(index);

                MotionEvent.PointerProperties pointerProperties = new MotionEvent.PointerProperties();
                event.getPointerProperties(index, pointerProperties);

                if (mainPointerId != pointerId) {

                    b = onPointerDown(pointerId, rX, rY);
                }

                mPreviousRX.put(pointerId, rX);
                mPreviousRY.put(pointerId, rY);
                mPreviousX.put(pointerId, x);
                mPreviousY.put(pointerId, y);

                break;
            case MotionEvent.ACTION_POINTER_UP:
                index = event.getActionIndex();
                // x, y é a posição do dedo em coordenada de pixel
                x = event.getX(index);
                y = event.getY(index);

                // rX, rY é a posição do dedo nas coordenadas da tela
                rX = (2 * x - width) / width;
                rY = (height - 2 * y) / height;

                pointerId = event.getPointerId(index);

                if (mainPointerId != pointerId) {

                    b = onPointerUp(pointerId);
                }

                mPreviousRX.put(pointerId, rX);
                mPreviousRY.put(pointerId, rY);
                mPreviousX.put(pointerId, x);
                mPreviousY.put(pointerId, y);
                break;
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

    public boolean onDown(int pointerId, float x, float y) {
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