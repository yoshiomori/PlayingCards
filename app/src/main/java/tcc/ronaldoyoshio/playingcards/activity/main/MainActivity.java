package tcc.ronaldoyoshio.playingcards.activity.main;

import android.app.Activity;
import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLScreen;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

public class MainActivity extends Activity {
    GLScreen screen;
    PlayingCards cards;
    CardImage cardImage = new CardImage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screen = new GLScreen(this);
        screen.setImages(
                new BackGround(),
                cardImage
        );
        setContentView(screen);
        screen.setSaveEnabled(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("cards")) {
                cards = new PlayingCards(savedInstanceState.getStringArrayList("cards"));
            }
        }
        else {
            cards = new PlayingCards();
        }
        cardImage.print(cards, CardImage.CENTERED);
//        cards.shuffle();
//        cardImage.print(cards);
//        cards.remove("Joker Black");
//        cardImage.print(cards);
//        cards.clear();
//        cards.add("Joker Black");
//        cardImage.print(cards);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("cards", cards);
        super.onSaveInstanceState(outState);
    }
}
