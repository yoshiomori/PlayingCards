package tcc.ronaldoyoshio.playingcards.activity.hand;

import android.os.Bundle;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.touchEventHandler.SendCard;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;

public class HandActivity extends GLActivity {
    MotionCardImage motionCardImage;

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

            motionCardImage = new MotionCardImage(this);
            motionCardImage.setOnSendCard(new SendCard(motionCardImage, playersName, directions));
            addImage(motionCardImage);
        }

        super.onCreate(savedInstanceState);
    }

    public void onReceiveCard(ArrayList<String> cards) {
        for (String card :
                cards) {
            motionCardImage.addCard(card);
        }
    }
}
