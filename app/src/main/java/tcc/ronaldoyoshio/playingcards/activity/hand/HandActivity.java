package tcc.ronaldoyoshio.playingcards.activity.hand;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;

public class HandActivity extends GLActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGround());
        addImage(new CardImage());

        super.onCreate(savedInstanceState);
    }
}
