package tcc.ronaldoyoshio.playingcards.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.BroadcastReceiver.WiFiDirectBroadcastReceiver;
import tcc.ronaldoyoshio.playingcards.R;

public abstract class AbstractGameConfigurationActivity extends Activity implements ChannelListener, PeerListListener {

    protected static final String TAG = "GameConfigActivity";
    protected WifiP2pManager manager;
    protected Channel channel;
    protected List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    protected WifiP2pDevice thisDevice;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    protected BroadcastReceiver receiver = null;
    private final IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
        findPeers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onChannelDisconnected() {
        if (manager != null && !retryChannel) {
            System.out.println("Caiu. Tentando reconectar");
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            System.out.println("Caiu. Já era");
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        if (peers.size() == 0) {
            Log.d(TAG, "Nenhum dispositivo encontrado");
            return;
        }
    }

    public List<WifiP2pDevice> getPeers() {
        return peers;
    }

    public void setPeers(List<WifiP2pDevice> peers) {
        this.peers = peers;
    }


    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void findPeers() {
        manager.discoverPeers(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Descoberta Iniciada com sucesso");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Descoberta Iniciada sem sucesso");
            }
        });
    }

    public String getThisDeviceName() {
        return thisDevice.deviceName;
    }

    public void setThisDevice(WifiP2pDevice thisDevice) {
        this.thisDevice = thisDevice;
    }
}
