package tcc.ronaldoyoshio.playingcards.activity.main;

import android.app.Activity;
import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLScreen;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

public class MainActivity extends Activity {
    GLScreen screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CardImage cardImage = new CardImage();

        screen = new GLScreen(this);
        screen.setImages(
                new BackGround(),
                cardImage
        );
        setContentView(screen);
        screen.setSaveEnabled(true);

        PlayingCards cards = new PlayingCards();
        cardImage.print(cards, CardImage.SIDEBYSIDE);
//        cards.shuffle();
//        cardImage.print(cards);
//        cards.remove("Joker Black");
//        cardImage.print(cards);
//        cards.clear();
//        cards.add("Joker Black");
//        cardImage.print(cards);
    }
}
