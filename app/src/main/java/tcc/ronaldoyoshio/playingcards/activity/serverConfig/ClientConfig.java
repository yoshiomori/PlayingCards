package tcc.ronaldoyoshio.playingcards.activity.serverConfig;

import android.content.Intent;
import android.view.View;

import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;

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
                Intent intent = new Intent(clientConfig, HandActivity.class);
                clientConfig.startActivity(intent);
            }
        });
    }
}
