package tcc.ronaldoyoshio.playingcards.activity.config.server;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.serverconfig);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        setListAdapter(adapter);
        super.onCreate(savedInstanceState);
        PackageManager pManager = this.getPackageManager();
        pManager.setComponentEnabledSetting(new ComponentName(getApplicationContext(), GameServerService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Intent intent = new Intent(this, GameServerService.class);
        startService(intent);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void waitPlayersConf (View view) {
        Message msg = Message.obtain();
        msg.what = GameServerService.MSG_STOP_SOCKET;
        sendMessageToService(msg);

        Button button = (Button) findViewById(R.id.buttonFinish);
        button.setEnabled(false);
        button.setVisibility(View.GONE);

        TextView textView = (TextView) findViewById(R.id.wait);
        textView.setText(R.string.waiting_confirm);
    }

    @Override
    protected void startTouchActivity() {
        final ServerConfigActivity serverConfig = this;
        Intent intent = new Intent(serverConfig, TouchConfigActivity.class);
        intent.putStringArrayListExtra(
                "playersName",
                items
        );
        intent.putExtra("nextActivity", SelectCardsActivity.class);
        serverConfig.startActivity(intent);
    }

    @Override
    public void nextView(View view) {
        super.nextView(view);
        bindService(new Intent(this, GameServerService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean handleMessage(Message msg) {
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
        return true;
    }
}
