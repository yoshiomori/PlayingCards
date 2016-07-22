package tcc.ronaldoyoshio.playingcards.activity.main;

import android.app.Activity;
import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLScreen;

/**
 * Responsável por desenhar o menu principal e tratar dos eventos de toques na tela.
 * Created by mori on 21/07/16.
 */
public class MainMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLScreen screen = new GLScreen(this);

        screen.setImages(
                new BackGround(),
                new ButtonImage(this)
        );

        setContentView(screen);
    }
}
