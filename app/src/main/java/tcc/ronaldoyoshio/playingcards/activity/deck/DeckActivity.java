package tcc.ronaldoyoshio.playingcards.activity.deck;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLActivity;
import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.model.Hand;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

public class DeckActivity extends GLActivity {
    Hand cards;
    CardImage cardImage = new DeckCardImage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImages(new BackGround(), cardImage);
        Bundle extras = getIntent().getExtras();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("cards")) {
                cards = new PlayingCards(savedInstanceState.getStringArrayList("cards"));
            }
        } else if (extras.containsKey("cards")) {
            cards = new PlayingCards(extras.getStringArrayList("cards"));
        } else {
            cards = new PlayingCards();
        }

        cardImage.print(cards, CardImage.CENTERED);
//        cards.shuffle();
//        deckCardImage.print(cards);
//        cards.remove("Joker Black");
//        deckCardImage.print(cards);
//        cards.clear();
//        cards.add("Joker Black");
//        deckCardImage.print(cards);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("cards", cards);
        super.onSaveInstanceState(outState);
    }
}
