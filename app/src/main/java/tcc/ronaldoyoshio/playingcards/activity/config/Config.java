package tcc.ronaldoyoshio.playingcards.activity.config;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.service.GameService;

/**
 * Activity para configuração
 * Created by mori on 27/08/16.
 */
public abstract class Config extends ListActivity {
    protected boolean mBound = false;
    protected Messenger mService = null;

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
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };
}
