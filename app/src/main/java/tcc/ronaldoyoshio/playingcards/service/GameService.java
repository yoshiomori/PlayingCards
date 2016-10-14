package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.client.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.broadcastReceiver.WiFiDirectBroadcastReceiver;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public abstract class GameService extends Service implements ConnectionInfoListener, Handler.Callback {
    public static final int MSG_CLIENT = 0;
    public static final int MSG_WIFI_DIRECT_SERVICE = 1;
    public static final int MSG_SUCCESS = 2;
    public static final int MSG_FAILED = 3;
    protected static final String SERVICE_REG_TYPE = "_presence._tcp";
    protected static final String LISTEN_PORT = "4545";
    public static final int MSG_SEND_CARD = 6;
    protected String name;

    protected WifiP2pManager manager;
    protected final IntentFilter intentFilter = new IntentFilter();
    protected Channel channel;
    protected BroadcastReceiver receiver = null;
    private boolean wifiDirectEnabled = false;
    protected Messenger mActivity = null;
    protected Map<String, WiFiP2pDiscoveredService> discoveredServices = new HashMap<>();
    protected WifiP2pDnsSdServiceRequest serviceRequest;

    private final Handler handler = new Handler(this);
    protected final Messenger mMessenger = new Messenger(handler);

    public void setIsiWfiDirectEnabled(boolean b) {
        this.wifiDirectEnabled = b;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getTag(), "Service onStartCommand");
        return Service.START_STICKY;
    }

    protected abstract String getServiceInstance();

    protected abstract String getTag();

    protected void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("LISTEN_PORT", String.valueOf(LISTEN_PORT));
        record.put("NAME", name);

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
                Log.d(getTag(), device.deviceAddress);
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

    @Override
    public boolean handleMessage(Message msg) {
        Message response;
        switch (msg.what) {
            case MSG_SUCCESS:
                Log.d(getTag(), msg.getData().getString("Mensagem"));
                break;
            case MSG_FAILED:
                Log.d(getTag(), msg.getData().getString("Mensagem"));
                break;
            case MSG_CLIENT:
                mActivity = msg.replyTo;
                name = (msg.getData().getString("Name") != null) ? msg.getData().getString("Name") : name;
                response = Message.obtain();
                response.what = ConfigActivity.MSG_SERVICE_CONNECTED;
                sendMessageToActivity(response);
                Log.d(getTag(), "Activity adicionada");
                if (msg.arg1 == 0) {
                    wifiP2pInit();
                    registerWiFiDirectBroadcastReceiver();
                    break;
                }
                break;
            case MSG_WIFI_DIRECT_SERVICE:
                if (wifiDirectEnabled) {
                    startRegistration();
                    startDiscoverService();
                    response = Message.obtain();
                    response.what = ConfigActivity.MSG_WIFI_DIRECT_OK;
                    sendMessageToActivity(response);
                    Log.d(getTag(), "WifiDirect OK");
                }
                else {
                    response = Message.obtain();
                    response.what = ConfigActivity.MSG_WIFI_DIRECT_NOK;
                    sendMessageToActivity(response);
                    Log.d(getTag(), "WifiDirect NOK");
                }
                break;
        }
        return true;
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
            msg.what = ClientConfigActivity.MSG_NEW_DEVICE;
            Bundle bundle = new Bundle();
            bundle.putParcelable("Device", (Parcelable) service);
            msg.setData(bundle);
            sendMessageToActivity(msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLooking();
        unregisterReceiver(receiver);
    }

    public void stopLooking() {
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
