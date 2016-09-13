package tcc.ronaldoyoshio.playingcards.activity.serverConfig;

import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import tcc.ronaldoyoshio.playingcards.activity.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.select.SelectCardsActivity;
import tcc.ronaldoyoshio.playingcards.activity.touchConfig.TouchConfigActivity;

/**
 * Activity para configurar o servidor
 * Created by mori on 26/08/16.
 */
public class ServerConfigActivity extends ConfigActivity {
    public ServerConfigActivity() {
        putItem("Pronto", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServerConfigActivity.this, TouchConfigActivity.class);
                intent.putStringArrayListExtra(
                        "playersName",
                        new ArrayList<>(Arrays.asList(
                                new String[]{"João", "Maria", "Bruxa"}
                        ))
                );
                intent.putExtra("nextActivity", SelectCardsActivity.class);
                ServerConfigActivity.this.startActivity(intent);
            }
        });
    }
}
