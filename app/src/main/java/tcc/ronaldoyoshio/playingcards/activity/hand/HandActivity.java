package tcc.ronaldoyoshio.playingcards.activity.hand;

import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLImage;
import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;

public class HandActivity extends GLActivity {
    @Override
    protected GLImage[] getImages() {
        return new GLImage[]{new BackGround(), new CardImage()};
    }
}
