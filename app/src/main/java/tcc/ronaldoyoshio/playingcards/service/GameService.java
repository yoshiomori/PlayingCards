package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
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
    public static final int MSG_SEND_CARD = 2;
    protected static final String SERVICE_REG_TYPE = "_presence._tcp";
    protected final Handler handler = new Handler(this);
    protected final Messenger mMessenger = new Messenger(handler);
    private final IntentFilter intentFilter = new IntentFilter();

    private Messenger mActivity;
    private boolean wifiDirectEnabled = false;

    private BroadcastReceiver receiver;
    protected WifiP2pDnsSdServiceRequest serviceRequest;
    protected WifiP2pManager manager;
    protected WifiP2pDnsSdServiceInfo service;
    protected Channel channel;
    protected String name;
    protected Map<String, WiFiP2pDiscoveredService> discoveredServices = new HashMap<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getTag(), "Service onStartCommand");
        return Service.START_STICKY;
    }

    private void wifiP2pInit() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    private void registerWiFiDirectBroadcastReceiver() {
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    private void startDiscoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice device) {
                Log.d(getTag(), fullDomainName);
                Log.d(getTag(), device.deviceAddress);
                WiFiP2pDiscoveredService service = new WiFiP2pDiscoveredService(record.get("NAME"), device);
                if (record.containsKey("LISTEN_PORT")) {
                    service.setPort(Integer.parseInt(record.get("LISTEN_PORT")));
                }
                discoveredServices.put(device.deviceAddress, service);
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                Log.d(getTag(), instanceName);
                if (discoveredServices.containsKey(srcDevice.deviceAddress)) {
                    WiFiP2pDiscoveredService service = discoveredServices.get(srcDevice.deviceAddress);
                    service.setInstanceName(instanceName);
                    sendDiscoveredServiceMessage(service);
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
                    public void onFailure(int reason) {
                        Log.d(getTag(), "Requisição adicionado sem sucesso: " + reason);
                        sendToastMessage("Erro na inicialização WifiDirect. Tente Novamente", ConfigActivity.MSG_ERROR);
                    }
                });
        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(getTag(), "Iniciando procura de serviços");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(getTag(), "Falha na procura de serviços: " + reason);
                sendToastMessage("Erro na inicialização WifiDirect. Tente Novamente", ConfigActivity.MSG_ERROR);
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        Message response;
        switch (msg.what) {
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
            sendToastMessage("Erro ao enviar Mensagem. Tente Novamente", ConfigActivity.MSG_ERROR);
        }
    }

    protected void sendDiscoveredServiceMessage(WiFiP2pDiscoveredService service) {
        if (getServiceInstance().equals(GamePlayerService.SERVICE_INSTANCE)) {
            Message msg = Message.obtain();
            msg.what = ClientConfigActivity.MSG_NEW_DEVICE;
            Bundle bundle = new Bundle();
            bundle.putParcelable("Device", service);
            msg.setData(bundle);
            sendMessageToActivity(msg);
        }
    }


    protected void cleanWifiP2P() {
        if (manager != null && channel != null) {
           manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(getTag(), "Finalizando a procura de dispositivos");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(getTag(), "Falha na finalizacao de procura de disposiivos: " + reason);
                }
            });

            manager.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(getTag(), "Limpando Servicos Locais");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(getTag(), "Falha na Limpeza de Servicos: " + reason);
                }
            });

            manager.clearServiceRequests(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(getTag(), "Limpando Requisições");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(getTag(), "Falha na Limpeza de Requisições: " + reason);
                }
            });

            if (service != null) {
                manager.removeLocalService(channel, service, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(getTag(), "Limpando Requisições");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(getTag(), "Falha na Limpeza de Requisições: " + reason);
                    }
                });
            }

            if (serviceRequest != null) {
                manager.removeServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(getTag(), "Service Request removido");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(getTag(), "Falha ao remover serviceRequest: " + reason);
                        sendToastMessage("Erro na Conexão com Servidor. Tente Novamente", ConfigActivity.MSG_ERROR);
                    }
                });
            }
        }
    }

    public void setIsiWfiDirectEnabled(boolean b) {
        this.wifiDirectEnabled = b;
    }

    protected void sendToastMessage(String message, Integer error) {
        Message msg = Message.obtain();
        msg.what = ConfigActivity.MSG_TEXT;
        msg.arg1 = error;
        Bundle bundle = new Bundle();
        bundle.putString("Mensagem", message);
        msg.setData(bundle);
        sendMessageToActivity(msg);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mActivity = null;
        super.onUnbind(intent);
        return false;
    }

    public void onTaskRemoved (Intent rootIntent){
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        cleanWifiP2P();
        handler.removeCallbacks(null);
        super.onDestroy();
    }

    protected abstract String getServiceInstance();

    protected abstract String getTag();

    protected abstract void startRegistration();
}
