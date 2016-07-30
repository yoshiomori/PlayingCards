package tcc.ronaldoyoshio.playingcards.GL;

import android.app.Activity;
import android.os.Bundle;

/**
 * Activity para uso com a biblioteca gráfica.
 * Created by mori on 30/07/16.
 */
public abstract class GLActivity extends Activity{
    public abstract GLImage[] getImages();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLScreen screen = new GLScreen(this);
        screen.setImages(getImages());
        setContentView(screen);
    }
}
