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
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;
import tcc.ronaldoyoshio.playingcards.model.web.server.ServerInterface;

public class GameServerService extends GameService {
    private static final String SERVICE_INSTANCE = "_gameServer";
    private static final String TAG = "GameServerService";

    @Override
    protected void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("LISTEN_PORT", String.valueOf(SERVER_PORT));
        record.put("NAME", "Servidor");
        record.put("AVALIABLE", "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Serviço Local Adicionado");
            }

            @Override
            public void onFailure(int error) {
                Log.d(TAG, "Falha ao adicionar o serviço");
            }
        });
    }

    protected void startDiscoverService() {
        DnsSdTxtRecordListener txtListener = new DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomainName, Map<String, String> record,
                    WifiP2pDevice device) {

            }
        };

        DnsSdServiceResponseListener servListener = new DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName,
                                                String registrationType, WifiP2pDevice srcDevice) {

                if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                    WiFiP2pDiscoveredService service = new WiFiP2pDiscoveredService(srcDevice, instanceName, registrationType);
                    discoveredServices.add(service);
                }

            }
        };
        manager.setDnsSdResponseListeners(channel, servListener, txtListener);

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        manager.addServiceRequest(channel, serviceRequest,
                new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Serviço Local Adicionado");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Log.d(TAG, "Serviço Local Adicionado");
                    }
                });
        manager.discoverServices(channel, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Serviço Local Adicionado");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "Serviço Local Adicionado");

            }
        });
    }
}
