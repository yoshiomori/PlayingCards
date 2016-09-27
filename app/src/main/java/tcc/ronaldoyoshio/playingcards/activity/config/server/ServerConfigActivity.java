package tcc.ronaldoyoshio.playingcards.activity.config.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.select.SelectCardsActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.touch.TouchConfigActivity;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;

/**
 * Activity para configurar o servidor
 * Created by mori on 26/08/16.
 */
public class ServerConfigActivity extends ConfigActivity {
    private static final String TAG = "ServerConfigActivity";
    final Messenger mMessenger = new Messenger(new ServerConfigIncomingHandler());

    public ServerConfigActivity() {
        /*putItem("Pronto", new View.OnClickListener() {
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
        });*/
    }

    protected void aux() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.serverconfig);
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, GameServerService.class);
        startService(intent);
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

    @Override
    protected String getTag() {
        return TAG;
    }

    class ServerConfigIncomingHandler extends ConfigActivity.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                default: super.handleMessage(msg);
            }
        }
    }
}
