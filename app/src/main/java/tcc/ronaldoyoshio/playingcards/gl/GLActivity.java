package tcc.ronaldoyoshio.playingcards.gl;

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
    final ArrayList<GLImage> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screen = new GLScreen(this);
        screen.getRenderer().setImages(images);
        ArrayList<TouchEventHandler> touchEventHandlers = new ArrayList<>();
        for (GLImage image :
                images) {
            final ArrayList<TouchEventHandler> imageTouchEventHandlers = image.getTouchEventHandlers();
            for (TouchEventHandler touchEventHandler:
                    imageTouchEventHandlers) {
                touchEventHandler.setScreen(screen);
            }
            touchEventHandlers.addAll(imageTouchEventHandlers);
            image.setResources(getResources());
        }
        screen.setTouchEventHandlers(touchEventHandlers);
        setContentView(screen);
    }

    protected void addImage(GLImage image) {
        images.add(image);
    }

    public GLScreen getScreen() {
        return screen;
    }
}
