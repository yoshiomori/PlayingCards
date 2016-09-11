package tcc.ronaldoyoshio.playingcards.activity.clientConfig;

import android.os.Bundle;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;

/**
 * Configura para onde a carta será enviada.
 * Created by mori on 10/09/16.
 */
public class TouchConfigActivity extends GLActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage deve ser chamado antes do onCreate */
        addImage(new BackGround());

        Bundle extras = getIntent().getExtras();
        ArrayList<String> playersName;
        if (extras.containsKey("playersName")) {
            playersName = extras.getStringArrayList("playersName");
            System.out.println(playersName);
            if (playersName != null && !playersName.isEmpty()) {
                addImage(new TextureImage(playersName.get(2), 100));
            }
        }

        super.onCreate(savedInstanceState);
    }
}
