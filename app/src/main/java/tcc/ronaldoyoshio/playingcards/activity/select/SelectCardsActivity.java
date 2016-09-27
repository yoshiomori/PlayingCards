package tcc.ronaldoyoshio.playingcards.activity.select;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.images.CardImage;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.model.Cards;

/**
 * Activity para selecionar as cartas a serem usadas pelo servidor.
 * Created by mori on 30/07/16.
 */
public class SelectCardsActivity extends GLActivity {
    SelectCardImage selectCardImage = new SelectCardImage();
    Cards cards = new Cards();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGroundImage());
        addImage(selectCardImage);
        addImage(new SelectButtonImage(this, cards, getIntent().getExtras()));

        super.onCreate(savedInstanceState);
        print(cards);
    }

    private void print(Cards playingCards) {
        selectCardImage.setCards(playingCards);
        selectCardImage.setTotalCards(playingCards.size());
        selectCardImage.setMode(CardImage.SIDEBYSIDE);
        getScreen().requestRender();
    }
}
