package tcc.ronaldoyoshio.playingcards.activity.hand;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLActivity;
import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;

public class HandActivity extends GLActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardImage cardImages = new CardImage();
        setImages(new BackGround(), cardImages);
    }
}
