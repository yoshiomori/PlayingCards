package tcc.ronaldoyoshio.playingcards.activity.config.client;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.touch.TouchConfigActivity;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;

/**
 * Configuração do cliente.
 * Created by mori on 27/08/16.
 */
public class ClientConfigActivity extends ConfigActivity {
    public static final int MSG_DEVICES = 4;
    private static final String TAG = "ClientConfigActivity";
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

    @Override
    protected String getTag() {
        return TAG;
    }

    class PlayerConfigIncomingHandler extends ConfigActivity.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case MSG_DEVICES:
                    HashMap<String, WiFiP2pDiscoveredService> map = (HashMap) msg.obj;
                    for (String address : map.keySet()) {
                        if (!discoveredDevices.containsKey(address)) {
                            String name = map.get(address).getName();
                            discoveredDevices.put(address, name);
                            putItem(name, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
