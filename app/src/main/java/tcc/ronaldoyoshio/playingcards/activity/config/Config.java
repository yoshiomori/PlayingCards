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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import tcc.ronaldoyoshio.playingcards.service.GameService;

public abstract class Config extends ListActivity {
    protected boolean mBound = false;
    protected Messenger mService = null;
    public static final int MSG_SERVICECONNECTED = 0;

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void putItem(String item, View.OnClickListener action){
        items.add(item);
        actions.add(action);
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            Message msg = Message.obtain();
            msg.arg1 = GameService.MSG_CLIENT;
            msg.replyTo = mMessenger;
            sendMessageToService(msg);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case MSG_SERVICECONNECTED:
                    System.out.println("Activity conectada");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public void sendMessageToService(Message msg) {
        if (!mBound) return;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
