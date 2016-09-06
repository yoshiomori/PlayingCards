package tcc.ronaldoyoshio.playingcards.activity.config;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;

import tcc.ronaldoyoshio.playingcards.activity.select.SelectCardsActivity;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;

/**
 * Activity para configurar o servidor
 * Created by mori on 26/08/16.
 */
public class ServerConfig extends Config {
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
}
