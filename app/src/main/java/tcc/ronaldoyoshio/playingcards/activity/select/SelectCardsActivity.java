package tcc.ronaldoyoshio.playingcards.activity.select;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.images.BackGround;
import tcc.ronaldoyoshio.playingcards.images.CardImage;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
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
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGround());
        addImage(selectCardImage);
        addImage(new SelectButtonImage(this, playingCards, getIntent().getExtras()));

        super.onCreate(savedInstanceState);
        print(playingCards);
    }

    private void print(Hand playingCards) {
        selectCardImage.setCards(playingCards);
        selectCardImage.setTotalCards(playingCards.size());
        selectCardImage.setMode(CardImage.SIDEBYSIDE);
        getScreen().requestRender();
    }
}
