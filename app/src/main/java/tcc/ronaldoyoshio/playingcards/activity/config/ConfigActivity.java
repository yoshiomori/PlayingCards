package tcc.ronaldoyoshio.playingcards.activity.config;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.service.GameService;

/**
 * Activity para configuracao
 * Created by mori on 27/08/16.
 */
public abstract class ConfigActivity extends ListActivity {
    protected boolean mBound = false;
    protected Messenger mService = null;
    public static final int MSG_SERVICE_CONNECTED = 0;
    public static final int MSG_WIFI_DIRECT_NOK = 1;
    public static final int MSG_SUCCESS = 2;
    public static final int MSG_FAILED = 3;
    protected Map<String, String> discoveredDevices = new HashMap<>();

    ArrayList<String> items = new ArrayList<>();
    ArrayList<View.OnClickListener> actions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setOnClickListener(actions.get(position));
                return textView;
            }
        });
    }
    protected void putItem(String item, View.OnClickListener action){
        items.add(item);
        actions.add(action);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected abstract Messenger getThisMessenger();

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            Message msg = Message.obtain();
            msg.arg1 = GameService.MSG_CLIENT;
            msg.replyTo = getThisMessenger();
            sendMessageToService(msg);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    protected abstract String getTag();

    protected class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case MSG_SERVICE_CONNECTED:
                    System.out.println("Activity conectada");
                    Message msgWifiDirect = Message.obtain();
                    msgWifiDirect.arg1 = GameService.MSG_WIFI_DIRECT_SERVICE;
                    sendMessageToService(msgWifiDirect);
                    break;
                case MSG_SUCCESS:
                    Log.d(getTag(), (String) msg.obj);
                    break;
                case MSG_FAILED:
                    Log.d(getTag(), (String) msg.obj);
                    break;
                case MSG_WIFI_DIRECT_NOK:
                    Message msgWifiDirectAgain = Message.obtain();
                    msgWifiDirectAgain.arg1 = GameService.MSG_WIFI_DIRECT_SERVICE;
                    sendMessageToService(msgWifiDirectAgain);
                    System.out.println("WifiDirect NOK");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void sendMessageToService(Message msg) {
        if (!mBound) return;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}