package tcc.ronaldoyoshio.playingcards.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import tcc.ronaldoyoshio.playingcards.service.GameService;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WiFitReceiver";
    private WifiP2pManager mManager;
    private Channel mChannel;
    private GameService service;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, GameService service) {
        super();
        this.turnOnWifi(service.getApplicationContext());
        this.mManager = manager;
        this.mChannel = channel;
        this.service = service;
    }

    private void turnOnWifi(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        Log.d(TAG, "Wifi Ligado");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG, "WifiDirect Ligado");
                service.setIsWifiP2pEnabled(true);
            } else {
                Log.d(TAG, "WifiDirect Desligado");
                service.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // TODO - Implementar o que acontece se a conexão cair
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // TODO - Implementar o que acontece se o dipositivo mudar
        }
    }
}
