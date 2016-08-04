package tcc.ronaldoyoshio.playingcards.GL;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;

/**
 * Activity para uso com a biblioteca gráfica.
 * Created by mori on 30/07/16.
 */
public abstract class GLActivity extends Activity{
    GLScreen screen;

    protected abstract GLImage[] getImages();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screen = new GLScreen(this);
        final GLImage[] images = getImages();
        screen.getRenderer().setImages(images);
        ArrayList<TouchEventHandler> touchEventHandlers = new ArrayList<>();
        for (GLImage image :
                images) {
            touchEventHandlers.addAll(image.getTouchEventHandlers());
            image.setContext(this);
        }
        screen.setTouchEventHandlers(touchEventHandlers);
        setContentView(screen);
    }

    public GLScreen getScreen() {
        return screen;
    }
}
