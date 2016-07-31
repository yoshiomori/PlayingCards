package tcc.ronaldoyoshio.playingcards.activity.main;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLActivity;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

/**
 * Activity para selecionar as cartas a serem usadas pelo servidor.
 * Created by mori on 30/07/16.
 */
public class SelectCardsActivity extends GLActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SelectCardImage SelectCardImage = new SelectCardImage();
        setImages(new BackGround(), SelectCardImage);
        SelectCardImage.print(new PlayingCards(), CardImage.SIDEBYSIDE);
    }
}
