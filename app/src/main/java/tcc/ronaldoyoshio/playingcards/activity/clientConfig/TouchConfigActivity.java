package tcc.ronaldoyoshio.playingcards.activity.clientConfig;

import android.os.Bundle;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.activity.deck.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.model.Hand;

/**
 * Configura para onde a carta será enviada.
 * Created by mori on 10/09/16.
 */
public class TouchConfigActivity extends GLActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage deve ser chamado antes do onCreate */
        addImage(new BackGround());
        CardImage card = new MotionCardImage(this);
        Hand hand = new Hand();
        hand.add("Back");
        card.setCards(hand);
        addImage(card);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> playersName;
        if (extras.containsKey("playersName")) {
            playersName = extras.getStringArrayList("playersName");
            System.out.println(playersName);
            if (playersName != null && !playersName.isEmpty()) {
                addImage(new TextureImage("Arraste a carta até a borda.", 100, 0, 2f / 3f));
                addImage(new TextureImage("Em direção:", 100, 0, 0));
                addImage(new TextureImage(playersName.get(2), 100, 0, -2f / 3f));
            }
        }

        super.onCreate(savedInstanceState);
    }
}
