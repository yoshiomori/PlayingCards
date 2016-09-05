package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public class GamePlayerService extends GameService {
    private static final String SERVICE_INSTANCE = "_gamePlayer";
    private static final String TAG = "GamePlayerService";

    @Override
    protected void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("LISTEN_PORT", String.valueOf(SERVER_PORT));
        record.put("NAME", "Cliente");
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

    @Override
    protected void startDiscoverService() {
WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
    @Override
    public void onDnsSdTxtRecordAvailable(
            String fullDomainName, Map<String, String> record,
            WifiP2pDevice device) {

    }
};

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
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
                new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Serviço Local Adicionado");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Log.d(TAG, "Serviço Local Adicionado");
                    }
                });
        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {

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
