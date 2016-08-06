package tcc.ronaldoyoshio.playingcards.activity.select;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLActivity;
import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.model.Hand;
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
        Hand playingCards = new PlayingCards();
        setImages(new BackGround(), SelectCardImage, new SelectButtonImage(this, playingCards));
        SelectCardImage.print(playingCards, CardImage.SIDEBYSIDE);
    }
}
