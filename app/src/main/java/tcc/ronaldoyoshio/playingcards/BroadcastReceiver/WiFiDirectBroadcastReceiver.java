package tcc.ronaldoyoshio.playingcards.BroadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private Activity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Activity activity) {
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

    /*WIFI_P2P_CONNECTION_CHANGED_ACTION	Broadcast when the state of the device's Wi-Fi connection changes.
    WIFI_P2P_PEERS_CHANGED_ACTION	Broadcast when you call discoverPeers(). You usually want to call requestPeers() to get an updated list of peers if you handle this intent in your application.
    WIFI_P2P_STATE_CHANGED_ACTION	Broadcast when Wi-Fi P2P is enabled or disabled on the device.
    WIFI_P2P_THIS_DEVICE_CHANGED_ACTION	Broadcast when a device's details have changed, such as the device's name*/

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
