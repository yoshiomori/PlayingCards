package tcc.ronaldoyoshio.playingcards.activity.clientConfig;

import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import tcc.ronaldoyoshio.playingcards.activity.Config;

/**
 * Configuração do cliente.
 * Created by mori on 27/08/16.
 */
public class ClientConfig extends Config {
    public ClientConfig() {
        final ClientConfig clientConfig = this;
        putItem("Pronto", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(clientConfig, TouchConfigActivity.class);
                intent.putStringArrayListExtra("playersName",
                        new ArrayList<>(Arrays.asList(new String[]{"João", "Maria", "Bruxa"})));
                clientConfig.startActivity(intent);
            }
        });
    }
}
