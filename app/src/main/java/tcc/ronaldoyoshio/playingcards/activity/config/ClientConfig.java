package tcc.ronaldoyoshio.playingcards.activity.config;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.view.View;

import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;

public class ClientConfig extends Config {
    final Messenger mMessenger = new Messenger(new PlayerConfigIncomingHandler());

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

    class PlayerConfigIncomingHandler extends Config.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
