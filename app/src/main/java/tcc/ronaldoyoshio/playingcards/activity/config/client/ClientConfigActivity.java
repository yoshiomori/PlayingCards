package tcc.ronaldoyoshio.playingcards.activity.config.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.touch.TouchConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;


public class ClientConfigActivity extends ConfigActivity {
    private static final String TAG = "ClientConfigActivity";
    public static final int MSG_CONNECT_OK = 5;
    public static final int MSG_CONNECT_NOK = 6;
    public static final int MSG_WEB_PLAYER = 7;
    public static final int MSG_WEB_INIT = 8;
    final Messenger mMessenger = new Messenger(new PlayerConfigIncomingHandler());
    private String serverAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.clientconfig);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setOnClickListener(actions.get(position));
                return textView;
            }
        };
        setListAdapter(adapter);
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, GamePlayerService.class);
        startService(intent);
    }

    @Override
    public void nextView(View view) {
        super.nextView(view);
        bindService(new Intent(this, GamePlayerService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

    }

    protected void putItem(String item, View.OnClickListener action){
        items.add(item);
        actions.add(action);
    }

    @Override
    protected void startTouchActivity() {
        final ClientConfigActivity clientConfig = this;
        Intent intent = new Intent(clientConfig, TouchConfigActivity.class);
        intent.putStringArrayListExtra(
                "playersName",
                items
        );
        intent.putExtra("nextActivity", HandActivity.class);
        clientConfig.startActivity(intent);
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
            switch (msg.what) {
                case MSG_NEW_DEVICE:
                    msg.getData().setClassLoader(WiFiP2pDiscoveredService.class.getClassLoader());
                    WiFiP2pDiscoveredService service = msg.getData().getParcelable("Device");
                    final String address = (service != null) ? service.getDevice().deviceAddress : "";
                    if (service.getInstanceName().equals(GameServerService.SERVICE_INSTANCE) && !discoveredDevices.containsKey(address)) {
                        String name = service.getName();
                        discoveredDevices.put(address, name);
                        putItem(name, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Message msg = Message.obtain();
                                msg.what = GamePlayerService.MSG_CONNECT_TO_DEVICE;
                                Bundle bundle = new Bundle();
                                bundle.putString("Address", address);
                                msg.setData(bundle);
                                sendMessageToService(msg);
                                TextView t = (TextView)findViewById(R.id.wait);
                                t.setText("Esperando Servidor");
                                serverAddress = address;
                                adapter.clear();
                                adapter.notifyDataSetChanged();
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_CONNECT_OK:
                    items.clear();
                    items.add(discoveredDevices.get(serverAddress));
                    break;
                case MSG_CONNECT_NOK:
                    TextView t = (TextView)findViewById(R.id.wait);
                    t.setText("Esperando Servidor");
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    response = Message.obtain();
                    response.what = GamePlayerService.MSG_REQUEST_DEVICES;
                    sendMessageToService(response);
                    break;
                case MSG_WIFI_DIRECT_OK:
                    ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipperClient);
                    flipper.showNext();
                    break;
                case MSG_WEB_PLAYER:
                    items.add(msg.getData().getString("Player"));
                    break;
                case MSG_WEB_INIT:
                    startTouchActivity();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
