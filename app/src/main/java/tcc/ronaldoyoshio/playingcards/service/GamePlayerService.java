package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public class GamePlayerService extends GameService {
    private static final String SERVICE_INSTANCE = "_gamePlayer";
    private static final String TAG = "GamePlayerService";
    private String name = "Client";
    protected Map<String, WiFiP2pDiscoveredService> discoveredServices = new HashMap<>();
    protected WifiP2pDnsSdServiceRequest serviceRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        startDiscoverService();
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
                    connectP2p(serv);
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


    public void connectP2p(WiFiP2pDiscoveredService service) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            Log.d(getTag(), "Service Request removido");
                        }

                        @Override
                        public void onFailure(int arg0) {
                            Log.d(getTag(), "Falha ao remover serviceRequest");
                        }
                    });

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(getTag(), "Conectando ao serviço");
            }

            @Override
            public void onFailure(int errorCode) {
                Log.d(getTag(), "Falha na conexão com serviço");
            }
        });
    }

    @Override
    protected String getName() {
        return this.name;
    }

    @Override
    protected String getServiceInstance() {
        return this.SERVICE_INSTANCE;
    }

    @Override
    protected String getTag() {
        return this.TAG;
    }
}
