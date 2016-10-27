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
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.application.PlayingCardsApplication;
import tcc.ronaldoyoshio.playingcards.service.GameService;

public abstract class ConfigActivity extends ListActivity implements Handler.Callback {
    public static final int MSG_SERVICE_CONNECTED = 0;
    public static final int MSG_WIFI_DIRECT_NOK = 1;
    public static final int MSG_WIFI_DIRECT_OK = 2;
    public static final int MSG_TEXT = 3;
    public static final int MSG_NEW_DEVICE = 4;
    public static final int MSG_ERROR = 5;

    private final Handler handler = new Handler(this);
    private final Messenger mMessenger = new Messenger(handler);
    private boolean mBound = false;
    private Messenger mService = null;
    protected ArrayAdapter adapter;
    protected ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText editText = (EditText)findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button button = (Button) findViewById(R.id.button);
                button.setEnabled((s.length() > 0));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void nextView(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        editText.setFocusable(false);
        button.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            EditText editText = (EditText) findViewById(R.id.editText);
            Bundle bundle = new Bundle();
            bundle.putString("Name", editText.getText().toString());

            Message msg = Message.obtain();
            msg.what = GameService.MSG_CLIENT;
            msg.arg1 = 0;
            msg.replyTo = mMessenger;
            msg.setData(bundle);
            sendMessageToService(msg);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        Message response;
        switch (msg.what) {
            case MSG_SERVICE_CONNECTED:
                response = Message.obtain();
                Log.d(getTag(), "Activity conectada");
                response.what = GameService.MSG_WIFI_DIRECT_SERVICE;
                sendMessageToService(response);
                break;
            case MSG_TEXT:
                String message = (msg.getData().getString("Mensagem") != null) ? msg.getData().getString("Mensagem") : "";
                Log.d(getTag(), message);
                Toast.makeText(getApplicationContext(), message,  Toast.LENGTH_SHORT).show();
                if (msg.arg1 == MSG_ERROR) finish();
                break;
            case MSG_WIFI_DIRECT_NOK:
                response = Message.obtain();
                response.what = GameService.MSG_WIFI_DIRECT_SERVICE;
                sendMessageToService(response);
                Log.d(getTag(), "WifiDirect NOK");
                break;
            case MSG_WIFI_DIRECT_OK:
                ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
                flipper.showNext();
                break;
        }
        return true;
    }

    public void sendMessageToService(Message msg) {
        if (!mBound) return;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (mBound) {
            unbindService(mConnection);
        }
        handler.removeCallbacks(null);
        super.onDestroy();
    }

    protected abstract void startTouchActivity();

    protected abstract String getTag();
}