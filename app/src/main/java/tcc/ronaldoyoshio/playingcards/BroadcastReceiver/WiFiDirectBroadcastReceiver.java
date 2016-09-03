package tcc.ronaldoyoshio.playingcards.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import tcc.ronaldoyoshio.playingcards.activity.AbstractGameConfigurationActivity;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WiFitReceiver";
    private WifiP2pManager mManager;
    private Channel mChannel;
    private AbstractGameConfigurationActivity activity;
    PeerListListener myPeerListListener;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, AbstractGameConfigurationActivity activity) {
        super();
        this.turnOnWifi(activity.getApplicationContext());
        this.mManager = manager;
        this.mChannel = channel;
        this.activity = activity;
    }

    private void turnOnWifi(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG, "WifiDirect Ligado");
                activity.setIsWifiP2pEnabled(true);
            } else {
                Log.d(TAG, "WifiDirect Desligado");
                activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null) {
                Log.d(TAG, "Mudança nos Peers");
                mManager.requestPeers(mChannel, activity);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // TODO - Implementar o que acontece se a conexão cair
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            activity.setThisDevice(device);
        }
    }
}
