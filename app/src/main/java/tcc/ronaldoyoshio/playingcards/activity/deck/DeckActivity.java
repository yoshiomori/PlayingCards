package tcc.ronaldoyoshio.playingcards.activity.deck;

import android.os.Bundle;
import android.view.KeyEvent;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.model.Hand;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

public class DeckActivity extends GLActivity {
    Hand cards;
    CardImage cardImage = new DeckCardImage(this);
    private ArrayList<String> playersName;
    private ArrayList<Integer> directions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGround());
        addImage(cardImage);

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("playersName")) {
            /* Recuperando a configuração do TouchConfigActivity, activity anterior */
                playersName = extras.getStringArrayList("playersName");

                System.out.println(playersName);
            }
            if (extras.containsKey("directions")) {
                directions = extras.getIntegerArrayList("directions");

                System.out.println(directions);
            }
            if (extras.containsKey("cards")) {
                cards = new PlayingCards(extras.getStringArrayList("cards"));
            }
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("cards")) {
                cards = new PlayingCards(savedInstanceState.getStringArrayList("cards"));
            }
        } else {
            cards = new PlayingCards();
        }
        print(cards);
//        cards.shuffle();
//        deckCardImage.print(cards);
//        cards.remove("Joker Black");
//        deckCardImage.print(cards);
//        cards.clear();
//        cards.add("Joker Black");
//        deckCardImage.print(cards);
    }

    private void print(Hand cards) {
        cardImage.setCards(cards);
        cardImage.setMode(CardImage.CENTERED);
        getScreen().requestRender();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("cards", cards);
        outState.putStringArrayList("playersName", playersName);
        outState.putIntegerArrayList("directions", directions);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
