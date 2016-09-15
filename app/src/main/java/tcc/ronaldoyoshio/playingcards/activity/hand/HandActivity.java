package tcc.ronaldoyoshio.playingcards.activity.hand;

import android.os.Bundle;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.activity.deck.SendCard;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;

public class HandActivity extends GLActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<String> playersName = null;
        ArrayList<Integer> directions = null;

        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGroundImage());

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("playersName")) {
            /* Recuperando a configuração do TouchConfigActivity, activity anterior */
                playersName = extras.getStringArrayList("playersName");
            }
            if (extras.containsKey("directions")) {
                directions = extras.getIntegerArrayList("directions");
            }

            MotionCardImage motionCardImage = new MotionCardImage(this);
            motionCardImage.setOnSendCard(new SendCard(motionCardImage, playersName, directions));
            addImage(motionCardImage);
        }

        super.onCreate(savedInstanceState);
    }
}
