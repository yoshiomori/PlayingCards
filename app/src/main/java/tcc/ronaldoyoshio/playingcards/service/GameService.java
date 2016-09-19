package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.client.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.broadcastReceiver.WiFiDirectBroadcastReceiver;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public abstract class GameService extends Service implements ConnectionInfoListener {
    public static final int MSG_CLIENT = 0;
    public static final int MSG_WIFI_DIRECT_SERVICE = 1;
    public static final int MSG_SUCCESS = 2;
    public static final int MSG_FAILED = 3;
    protected static final String SERVICE_REG_TYPE = "_presence._tcp";
    protected static final String LISTEN_PORT = "4545";

    protected WifiP2pManager manager;
    protected final IntentFilter intentFilter = new IntentFilter();
    protected Channel channel;
    protected BroadcastReceiver receiver = null;
    private boolean wifiDirectEnabled = false;
    protected Messenger mActivity = null;
    protected Map<String, WiFiP2pDiscoveredService> discoveredServices = new HashMap<>();
    protected WifiP2pDnsSdServiceRequest serviceRequest;

    public void setIsiWfiDirectEnabled(boolean b) {
        this.wifiDirectEnabled = b;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected abstract String getName();

    protected abstract String getServiceInstance();

    protected abstract String getTag();

    protected void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("LISTEN_PORT", String.valueOf(LISTEN_PORT));
        record.put("NAME", getName());

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(getServiceInstance(), SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(getTag(), "Serviço Local Adicionado");
            }

            @Override
            public void onFailure(int error) {
                Log.d(getTag(), "Falha ao adicionar o serviço");
            }
        });
    }

    protected void wifiP2pInit() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    protected void registerWiFiDirectBroadcastReceiver() {
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    protected void startDiscoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice device) {
                Log.d(getTag(), fullDomainName);
                discoveredServices.put(device.deviceAddress, new WiFiP2pDiscoveredService(record.get("NAME"), Integer.parseInt(record.get("LISTEN_PORT"))));
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                Log.d(getTag(), instanceName);
                if (discoveredServices.containsKey(srcDevice.deviceAddress)) {
                    WiFiP2pDiscoveredService serv = discoveredServices.get(srcDevice.deviceAddress);
                    serv.setDevice(srcDevice);
                    serv.setInstanceName(instanceName);
                    serv.setServiceRegistrationType(registrationType);
                    sendDiscoveredServiceMessage(serv);
                }
            }
        };
        manager.setDnsSdResponseListeners(channel, servListener, txtListener);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.d(getTag(), "Requisição adicionado com sucesso");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Log.d(getTag(), "Requisição adicionado sem sucesso");
                    }
                });
        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(getTag(), "Iniciando procura de serviços");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(getTag(), "Falha na procura de serviços");
            }
        });
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Message response;
            switch (msg.arg1) {
                case MSG_SUCCESS:
                    Log.d(getTag(), msg.getData().getString("Mensagem"));
                    break;
                case MSG_FAILED:
                    Log.d(getTag(), msg.getData().getString("Mensagem"));
                    break;
                case MSG_CLIENT:
                    mActivity = msg.replyTo;
                    response = Message.obtain();
                    response.arg1 = ConfigActivity.MSG_SERVICE_CONNECTED;
                    sendMessageToActivity(response);
                    Log.d(getTag(), "Activity adicionada");
                    wifiP2pInit();
                    registerWiFiDirectBroadcastReceiver();
                    break;
                case MSG_WIFI_DIRECT_SERVICE:
                    if (wifiDirectEnabled) {
                        startRegistration();
                        startDiscoverService();
                        response = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("Mensagem", "WifiDirect OK");
                        response.setData(bundle);
                        response.arg1 = ConfigActivity.MSG_SUCCESS;
                        sendMessageToActivity(response);
                        Log.d(getTag(), "WifiDirect OK");
                    }
                    else {
                        response = Message.obtain();
                        response.arg1 = ConfigActivity.MSG_WIFI_DIRECT_NOK;
                        sendMessageToActivity(response);
                        Log.d(getTag(), "WifiDirect NOK");
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    protected void sendMessageToActivity(Message msg) {
        if (mActivity == null) return;
        try {
            mActivity.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void sendDiscoveredServiceMessage(WiFiP2pDiscoveredService service) {
        if (getServiceInstance().equals(GamePlayerService.SERVICE_INSTANCE)) {
            Message msg = Message.obtain();
            msg.arg1 = ClientConfigActivity.MSG_NEW_DEVICE;
            Bundle bundle = new Bundle();
            bundle.putParcelable("Device", (Parcelable) service);
            msg.setData(bundle);
            sendMessageToActivity(msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void stopLooking () {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

}
