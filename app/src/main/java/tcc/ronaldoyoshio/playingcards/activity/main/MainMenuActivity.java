package tcc.ronaldoyoshio.playingcards.activity.main;

import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLImage;
import tcc.ronaldoyoshio.playingcards.activity.BackGround;

/**
 * Responsável por desenhar o menu principal e tratar dos eventos de toques na tela.
 * Created by mori on 21/07/16.
 */
public class MainMenuActivity extends GLActivity {
    @Override
    protected GLImage[] getImages() {
        return new GLImage[]{
                new BackGround(),
                new MainMenuButtonImage(this)
        };
    }
}
