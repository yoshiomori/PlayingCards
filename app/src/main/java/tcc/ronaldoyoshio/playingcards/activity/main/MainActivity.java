package tcc.ronaldoyoshio.playingcards.activity.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.GL.GLScreen;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiDirectBroadcastReceiver;

public class MainActivity extends Activity {
    GLScreen screen;
    private WifiP2pManager mManager;
    private Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CardImage cardImage = new CardImage();

        screen = new GLScreen(this);
        screen.setImages(
                new BackGround(),
                cardImage
        );
        setContentView(screen);
        screen.setSaveEnabled(true);

        PlayingCards cards = new PlayingCards();
        cardImage.print(cards);
//        cards.shuffle();
//        cardImage.print(cards);
//        cards.remove("Joker Black");
//        cardImage.print(cards);
//        cards.clear();
//        cards.add("Joker Black");
//        cardImage.print(cards);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
