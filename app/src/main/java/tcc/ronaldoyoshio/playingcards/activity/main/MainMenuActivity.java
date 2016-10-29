package tcc.ronaldoyoshio.playingcards.activity.main;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.application.PlayingCardsApplication;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;

/**
 * Responsável por desenhar o menu principal e tratar dos eventos de toques na tela.
 */
public class MainMenuActivity extends GLActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGroundImage());
        addImage(new MainMenuButtonImage(this));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        PlayingCardsApplication.getInstance().stopServices();
        super.onResume();
    }
}
