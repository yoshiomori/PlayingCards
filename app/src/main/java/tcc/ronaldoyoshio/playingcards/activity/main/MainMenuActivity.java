package tcc.ronaldoyoshio.playingcards.activity.main;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;

/**
 * Responsável por desenhar o menu principal e tratar dos eventos de toques na tela.
 * Created by mori on 21/07/16.
 */
public class MainMenuActivity extends GLActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGroundImage());
        addImage(new MainMenuButtonImage(this));

        super.onCreate(savedInstanceState);
    }
}
