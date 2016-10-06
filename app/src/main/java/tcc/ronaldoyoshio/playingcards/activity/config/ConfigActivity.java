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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.service.GameService;

public abstract class ConfigActivity extends ListActivity {
    protected boolean mBound = false;
    protected Messenger mService = null;
    public static final int MSG_SERVICE_CONNECTED = 0;
    public static final int MSG_WIFI_DIRECT_NOK = 1;
    public static final int MSG_WIFI_DIRECT_OK = 2;
    public static final int MSG_TEXT = 3;
    public static final int MSG_NEW_DEVICE = 4;

    protected Map<String, String> discoveredDevices = new HashMap<>();

    protected ArrayAdapter adapter;
    protected ArrayList<String> items = new ArrayList<>();
    protected ArrayList<View.OnClickListener> actions = new ArrayList<>();

    public void nextView(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        editText.setFocusable(false);
        button.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected abstract void startTouchActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText editText = (EditText)findViewById(R.id.editText);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Button button = (Button) findViewById(R.id.button);
                    button.setEnabled((s.length() > 0));
                }

                @Override
                public void afterTextChanged(Editable s) {
            }
        });
    }

    protected abstract Messenger getThisMessenger();

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            EditText editText = (EditText) findViewById(R.id.editText);
            Bundle bundle = new Bundle();
            bundle.putString("Name", editText.getText().toString());

            Message msg = Message.obtain();
            msg.arg1 = GameService.MSG_CLIENT;
            msg.replyTo = getThisMessenger();
            msg.setData(bundle);
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
            Message response;
            switch (msg.arg1) {
                case MSG_SERVICE_CONNECTED:
                    response = Message.obtain();
                    Log.d(getTag(), "Activity conectada");
                    response.arg1 = GameService.MSG_WIFI_DIRECT_SERVICE;
                    sendMessageToService(response);
                    break;
                case MSG_TEXT:
                    Log.d(getTag(), msg.getData().getString("Mensagem"));
                    break;
                case MSG_WIFI_DIRECT_NOK:
                    response = Message.obtain();
                    response.arg1 = GameService.MSG_WIFI_DIRECT_SERVICE;
                    sendMessageToService(response);
                    Log.d(getTag(), "WifiDirect NOK");
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