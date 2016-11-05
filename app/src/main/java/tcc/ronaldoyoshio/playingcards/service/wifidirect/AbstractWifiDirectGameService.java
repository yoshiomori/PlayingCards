package tcc.ronaldoyoshio.playingcards.service.wifidirect;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.Method;

import tcc.ronaldoyoshio.playingcards.activity.config.AbstractConfigActivity;
import tcc.ronaldoyoshio.playingcards.broadcastReceiver.WebBroadcastReceiver;

public abstract class AbstractWifiDirectGameService extends Service implements ConnectionInfoListener, Handler.Callback {
    public static final int MSG_CLIENT = 0;
    public static final int MSG_WIFI_DIRECT_SERVICE = 1;
    public static final int MSG_SEND_CARD = 2;
    protected static final String SERVICE_REG_TYPE = "_presence._tcp";
    private final Handler handler = new Handler(this);
    protected final Messenger mMessenger = new Messenger(handler);
    private final IntentFilter intentFilter = new IntentFilter();

    private Messenger mActivity;
    private boolean wifiDirectEnabled = false;

    private BroadcastReceiver receiver;
    protected WifiP2pManager manager;
    protected Channel channel;
    protected String name;

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
        disconnect();
        deletePersistentGroups();
        cleanWifiP2P();
    }

    private void registerWiFiDirectBroadcastReceiver() {
        receiver = new WebBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    private void deletePersistentGroups(){
        try {
            Log.d(getTag(), "Destruindo Grupos");
            Method[] methods = WifiP2pManager.class.getMethods();
            for (Method method : methods) {
                if (method.getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        method.invoke(manager, channel, netid, null);
                    }
                }
            }
        } catch(Exception e) {
            String message = (e.getMessage() != null) ? e.getMessage() : "";
            Log.d(getTag(), message);
            sendToastMessage("Não foi possível limpar grupos persisitidos. Tente Novamente", AbstractConfigActivity.MSG_ERROR);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        Message response;
        switch (msg.what) {
            case MSG_WIFI_DIRECT_SERVICE:
                if (wifiDirectEnabled) {
                    startWifiP2p();
                    response = Message.obtain();
                    response.what = AbstractConfigActivity.MSG_WIFI_DIRECT_OK;
                    sendMessageToActivity(response);
                    Log.d(getTag(), "WifiDirect OK");
                }
                else {
                    response = Message.obtain();
                    response.what = AbstractConfigActivity.MSG_WIFI_DIRECT_NOK;
                    sendMessageToActivity(response);
                    Log.d(getTag(), "WifiDirect NOK");
                }
                break;
            case MSG_CLIENT:
                mActivity = msg.replyTo;
                name = (msg.getData().getString("Name") != null) ? msg.getData().getString("Name") : name;
                response = Message.obtain();
                response.what = AbstractConfigActivity.MSG_SERVICE_CONNECTED;
                sendMessageToActivity(response);
                Log.d(getTag(), "Activity adicionada");
                if (msg.arg1 == 0) {
                    wifiP2pInit();
                    registerWiFiDirectBroadcastReceiver();
                }
                break;
        }
        return true;
    }

    protected synchronized void sendMessageToActivity(Message msg) {
        if (mActivity == null) return;
        try {
            mActivity.send(msg);
        } catch (RemoteException e) {
            sendToastMessage("Erro ao enviar Mensagem. Tente Novamente", AbstractConfigActivity.MSG_ERROR);
        }
    }

    public void setIsiWfiDirectEnabled(boolean b) {
        this.wifiDirectEnabled = b;
    }

    protected void sendToastMessage(String message, Integer error) {
        Message msg = Message.obtain();
        msg.what = AbstractConfigActivity.MSG_TEXT;
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

    @Override
    public void onTaskRemoved (Intent rootIntent){
        disconnect();
        this.stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        cleanWifiP2P();
        disconnect();
        handler.removeCallbacks(null);
        super.onDestroy();
    }

    private void disconnect() {
        if (manager != null && channel != null) {
            manager.requestGroupInfo(channel, new GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && manager != null && channel != null && group.isGroupOwner()) {
                        manager.removeGroup(channel, new ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(getTag(), "Grupo Removido");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(getTag(), "Erro ao remover Grupo: " + reason);
                            }
                        });
                    }
                }
            });
        }
    }

    protected abstract String getServiceInstance();

    protected abstract String getTag();

    protected abstract void startWifiP2p();

    protected abstract void cleanWifiP2P();
}
