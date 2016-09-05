package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public abstract class GameService extends Service {
    protected static final String SERVICE_REG_TYPE = "_presence._tcp";
    protected static final String SERVER_PORT = "4545";
    protected WifiP2pManager manager;
    protected WifiP2pDnsSdServiceRequest serviceRequest;
    protected final IntentFilter intentFilter = new IntentFilter();
    protected WifiP2pManager.Channel channel;
    protected BroadcastReceiver receiver = null;
    protected List<WiFiP2pDiscoveredService> discoveredServices = new ArrayList<>();
    private boolean wifiP2pEnabled = false;
    protected Messenger activity = null;

    public void setIsWifiP2pEnabled(boolean b) {
        this.wifiP2pEnabled = b;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        startRegistration();
        startDiscoverService();
    }

    protected abstract void startRegistration();
    protected abstract void startDiscoverService();

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    protected void sendMessageToUI(String message) {
        
    }
}
