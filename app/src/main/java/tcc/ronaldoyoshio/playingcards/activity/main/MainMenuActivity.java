package tcc.ronaldoyoshio.playingcards.activity.main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.application.PlayingCardsApplication;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;

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

    @Override
    protected void onResume() {
        PlayingCardsApplication.getInstance().stopServices();
        super.onResume();
    }
}
