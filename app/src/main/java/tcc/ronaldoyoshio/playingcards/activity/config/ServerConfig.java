package tcc.ronaldoyoshio.playingcards.activity.config;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;

import tcc.ronaldoyoshio.playingcards.activity.select.SelectCardsActivity;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;

public class ServerConfig extends Config {
    final Messenger mMessenger = new Messenger(new ServerConfigIncomingHandler());

    public ServerConfig() {
        final ServerConfig serverConfig = this;
        putItem("Pronto", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(serverConfig, SelectCardsActivity.class);
                serverConfig.startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, GameServerService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected Messenger getThisMessenger() {
        return mMessenger;
    }

    class ServerConfigIncomingHandler extends Config.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
