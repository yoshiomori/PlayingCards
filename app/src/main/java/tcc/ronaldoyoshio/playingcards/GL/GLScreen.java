package tcc.ronaldoyoshio.playingcards.GL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;

public class GLScreen extends GLSurfaceView {
    private final GLRenderer renderer;
    private ArrayList<TouchEventHandler> touchEventHandlers;

    public GLScreen(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        renderer = new GLRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    protected GLRenderer getRenderer(){
        return renderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);
        for (TouchEventHandler touchEventHandler :
                touchEventHandlers) {
            b |= touchEventHandler.onTouchEvent(event);
        }
        if (b) {
            requestRender();
        }
        return b;
    }

    public void setTouchEventHandlers(ArrayList<TouchEventHandler> touchEventHandlers) {
        this.touchEventHandlers = touchEventHandlers;
    }
}
