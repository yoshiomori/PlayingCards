package tcc.ronaldoyoshio.playingcards.activity.config.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.server.ServerConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.touch.TouchConfigActivity;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;
import tcc.ronaldoyoshio.playingcards.service.GameService;

/**
 * Configuração do cliente.
 * Created by mori on 27/08/16.
 */
public class ClientConfigActivity extends ConfigActivity {
    public static final int MSG_NEW_DEVICE = 4;

    private static final String TAG = "ClientConfigActivity";
    public static final int MSG_CONNECT_NOK = 6;
    final Messenger mMessenger = new Messenger(new PlayerConfigIncomingHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.clientconfig);
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, GamePlayerService.class);
        startService(intent);

    }

    @Override
    public void nextView(View view) {
        super.nextView(view);
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipperClient);
        //flipper.showNext();
    }

    public ClientConfigActivity() {
        /*final ClientConfigActivity clientConfig = this;
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
        });*/
    }

    protected void aux() {
        final ClientConfigActivity clientConfig = this;
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
            Message response;
            switch (msg.arg1) {
                case MSG_NEW_DEVICE:
                    msg.getData().setClassLoader(WiFiP2pDiscoveredService.class.getClassLoader());
                    WiFiP2pDiscoveredService service = (WiFiP2pDiscoveredService) msg.getData().getParcelable("Device");
                    final String address = service.getDevice().deviceAddress;
                    if (service.getInstanceName().equals(GameServerService.SERVICE_INSTANCE) && !discoveredDevices.containsKey(address)) {
                        String name = service.getName();
                        discoveredDevices.put(address, name);
                        putItem(name, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Message msg = Message.obtain();
                                msg.arg1 = GamePlayerService.MSG_CONNECT_TO_DEVICE;
                                Bundle bundle = new Bundle();
                                bundle.putString("Address", address);
                                msg.setData(bundle);
                                sendMessageToService(msg);
                                TextView t = (TextView)findViewById(R.id.wait);
                                t.setText("Esperando Servidor");
                                adapter.clear();
                                adapter.notifyDataSetChanged();
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_CONNECT_NOK:
                    TextView t = (TextView)findViewById(R.id.wait);
                    t.setText("Esperando Servidor");
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    response = Message.obtain();
                    response.arg1 = GamePlayerService.MSG_REQUEST_DEVICES;
                    sendMessageToService(response);
                    break;
                case 100:
                    aux();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
