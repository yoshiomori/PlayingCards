package tcc.ronaldoyoshio.playingcards.activity.serverConfig;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Activity para configuração
 * Created by mori on 27/08/16.
 */
public class Config extends ListActivity {
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
}
