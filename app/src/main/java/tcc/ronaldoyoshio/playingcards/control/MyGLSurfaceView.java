package tcc.ronaldoyoshio.playingcards.control;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import tcc.ronaldoyoshio.playingcards.model.PlayingCards;
import tcc.ronaldoyoshio.playingcards.view.HandGLRenderer;

public class MyGLSurfaceView extends GLSurfaceView {

    private final HandGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new HandGLRenderer(context);
        setRenderer(mRenderer);

        PlayingCards deck = new PlayingCards();
        deck.Shuffle();
        while (!deck.isEmpty())
            mRenderer.addCard(deck.Draw());

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = (2 * e.getX() - getWidth()) / getWidth();
        float y = (getHeight() - 2 * e.getY()) / getHeight();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                mRenderer.setPosition(dx, dy, x, y);
                requestRender();
                break;
            case MotionEvent.ACTION_DOWN:
                System.out.println("Dedo na tela!");
                mRenderer.activateCard(x, y);
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("Dedo saiu da tela!");
                mRenderer.deactivateCards();
                break;
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

}
