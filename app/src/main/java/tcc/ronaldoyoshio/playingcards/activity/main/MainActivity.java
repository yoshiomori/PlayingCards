package tcc.ronaldoyoshio.playingcards.activity.main;

import android.app.Activity;
import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLScreen;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CardImage cardImage = new CardImage();

        GLScreen screen = new GLScreen(this);
        screen.setImages(
                cardImage
        );
        setContentView(screen);

        PlayingCards cards = new PlayingCards();
        cardImage.print(cards);
        cards.shuffle();
        cardImage.print(cards);
        cards.remove("Joker Black");
        cardImage.print(cards);
        cards.clear();
        cards.add("Joker Black");
        cardImage.print(cards);
    }
}
