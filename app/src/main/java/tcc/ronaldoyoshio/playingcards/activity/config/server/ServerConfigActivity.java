package tcc.ronaldoyoshio.playingcards.activity.config.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.touch.TouchConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.select.SelectCardsActivity;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;

public class ServerConfigActivity extends ConfigActivity {
    private static final String TAG = "ServerConfigActivity";
    public static final int MSG_CONFIRM = 5;
    final Messenger mMessenger = new Messenger(new ServerConfigIncomingHandler());

    @Override
    public void nextView(View view) {
        super.nextView(view);
        bindService(new Intent(this, GameServerService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    public void waitPlayersConf (View view) {
        Message msg = Message.obtain();
        msg.what = GameServerService.MSG_STOP_SOCKET;
        sendMessageToService(msg);

        Button button = (Button) findViewById(R.id.buttonFinish);
        button.setEnabled(false);
        button.setVisibility(View.GONE);
        adapter.clear();
        TextView textView = (TextView) findViewById(R.id.wait);
        textView.setText("Esperando Confirmação");
    }

    @Override
    protected void startTouchActivity() {
        Intent intent = new Intent(ServerConfigActivity.this, TouchConfigActivity.class);
        intent.putStringArrayListExtra(
                "playersName",
                items
        );
        intent.putExtra("nextActivity", SelectCardsActivity.class);
        ServerConfigActivity.this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.serverconfig);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        setListAdapter(adapter);
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, GameServerService.class);
        startService(intent);
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
            switch (msg.what) {
                case MSG_WIFI_DIRECT_OK:
                    ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipperServer);
                    flipper.showNext();
                    Message response = Message.obtain();
                    response.what = GameServerService.MSG_SERVER_SOCKET;
                    sendMessageToService(response);
                    break;
                case MSG_NEW_DEVICE:
                    Button button = (Button) findViewById(R.id.buttonFinish);
                    button.setVisibility(View.VISIBLE);
                    String client = msg.getData().getString("Nome");
                    if (!items.contains(client)) {
                        items.add(client);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_CONFIRM:
                    startTouchActivity();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
