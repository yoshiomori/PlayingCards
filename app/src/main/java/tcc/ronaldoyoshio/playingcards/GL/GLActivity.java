package tcc.ronaldoyoshio.playingcards.GL;

import android.app.Activity;
import android.os.Bundle;

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
        screen.setImages(getImages());
        setContentView(screen);
    }
}
