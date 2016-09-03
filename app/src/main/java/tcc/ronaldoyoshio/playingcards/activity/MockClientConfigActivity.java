package tcc.ronaldoyoshio.playingcards.activity;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import tcc.ronaldoyoshio.playingcards.R;

public class MockClientConfigActivity extends AbstractGameConfigurationActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void connectToServer() {
        WifiP2pDevice server = peers.get(0);
        Log.d(TAG, server.deviceName);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = server.deviceAddress;
        manager.connect(channel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Servidor conectado com sucesso");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Falha ao conectar com servidor");
            }
        });
    }

    public void connectToServer(View view) {
        findPeers();
        connectToServer();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
}
