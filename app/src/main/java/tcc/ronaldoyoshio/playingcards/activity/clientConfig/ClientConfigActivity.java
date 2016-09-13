package tcc.ronaldoyoshio.playingcards.activity.clientConfig;

import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import tcc.ronaldoyoshio.playingcards.activity.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.activity.touchConfig.TouchConfigActivity;

/**
 * Configuração do cliente.
 * Created by mori on 27/08/16.
 */
public class ClientConfigActivity extends ConfigActivity {
    public ClientConfigActivity() {
        final ClientConfigActivity clientConfig = this;
        putItem("Pronto", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(clientConfig, TouchConfigActivity.class);
                intent.putStringArrayListExtra(
                        "playersName",
                        new ArrayList<>(Arrays.asList(
                                new String[]{"Maria", "Bruxa", "servidor(mesa)"}
                        ))
                );
                intent.putExtra("nextActivity", HandActivity.class);
                clientConfig.startActivity(intent);
            }
        });
    }
}
