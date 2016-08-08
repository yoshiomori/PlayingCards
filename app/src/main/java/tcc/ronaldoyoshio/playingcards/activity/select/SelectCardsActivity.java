package tcc.ronaldoyoshio.playingcards.activity.select;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLActivity;
import tcc.ronaldoyoshio.playingcards.GL.GLImage;
import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.model.Hand;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

/**
 * Activity para selecionar as cartas a serem usadas pelo servidor.
 * Created by mori on 30/07/16.
 */
public class SelectCardsActivity extends GLActivity {
    SelectCardImage selectCardImage = new SelectCardImage();
    Hand playingCards = new PlayingCards();
    @Override
    protected GLImage[] getImages() {
        return new GLImage[]{
                new BackGround(), selectCardImage, new SelectButtonImage(this, playingCards)
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        print(playingCards);
    }

    private void print(Hand playingCards) {
        selectCardImage.setCards(playingCards);
        selectCardImage.setMode(CardImage.SIDEBYSIDE);
        getScreen().requestRender();
    }
}
