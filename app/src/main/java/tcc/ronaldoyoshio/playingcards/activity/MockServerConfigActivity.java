package tcc.ronaldoyoshio.playingcards.activity;

import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.util.Log;

import tcc.ronaldoyoshio.playingcards.R;

public class MockServerConfigActivity extends AbstractGameConfigurationActivity {
    private static final String TAG = "ServerConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);
    }

    @Override
    public void onResume() {
        super.onResume();
        super.findPeers();
        super.manager.createGroup(super.channel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Grupo criado com sucesso");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Grupo criado sem sucesso");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}