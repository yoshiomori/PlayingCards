package tcc.ronaldoyoshio.playingcards.activity.config.client;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.touch.TouchConfigActivity;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;

/**
 * Configuração do cliente.
 * Created by mori on 27/08/16.
 */
public class ClientConfigActivity extends ConfigActivity {
    final Messenger mMessenger = new Messenger(new PlayerConfigIncomingHandler());

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

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, GamePlayerService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected Messenger getThisMessenger() {
        return mMessenger;
    }

    class PlayerConfigIncomingHandler extends ConfigActivity.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
