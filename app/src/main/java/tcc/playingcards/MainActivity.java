package tcc.playingcards;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import playingcards.Hand;
import playingcards.PlayingCards;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        TextView textView = (TextView) findViewById(R.id.textView);

        PlayingCards deck = new PlayingCards();
        Hand hand = new Hand();

        if (textView != null) {
            textView.setText("Embaralhando\n");
            deck.Shuffle();

            textView.append("Pegando uma carta do baralho\n");
            hand.Draw(deck.Draw());
            textView.append(hand.Show().toString() + "\n");

            textView.append("Pegando outra carta do baralho\n");
            hand.Draw(deck.Draw());
            textView.append(hand.Show().toString() + "\n");

            textView.append("Descartando a primeira carta da mão\n");
            hand.Discard(hand.Show().get(0));
            textView.append(hand.Show().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
