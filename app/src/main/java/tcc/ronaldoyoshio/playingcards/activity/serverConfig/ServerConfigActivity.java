package tcc.ronaldoyoshio.playingcards.activity.serverConfig;

import android.content.Intent;
import android.view.View;

import tcc.ronaldoyoshio.playingcards.activity.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.select.SelectCardsActivity;

/**
 * Activity para configurar o servidor
 * Created by mori on 26/08/16.
 */
public class ServerConfigActivity extends ConfigActivity {
    public ServerConfigActivity() {
        final ServerConfigActivity serverConfig = this;
        putItem("Pronto", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(serverConfig, SelectCardsActivity.class);
                serverConfig.startActivity(intent);
            }
        });
    }
}
