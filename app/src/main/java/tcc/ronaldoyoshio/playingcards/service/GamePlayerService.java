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
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.client.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public class GamePlayerService extends GameService {
    public static final int MSG_CONNECT_TO_DEVICE = 4;
    public static final int MSG_REQUEST_DEVICES = 5;
    private static final String SERVICE_INSTANCE = "_gamePlayer";
    private static final String TAG = "GamePlayerService";
    private String name = "Client";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                Message response = Message.obtain();
                response.arg1 = ConfigActivity.MSG_SUCCESS;
                response.obj = new String("Conectado com Servidor");
                sendMessageToActivity(response);
            }

            @Override
            public void onFailure(int errorCode) {
                Log.d(getTag(), "Falha na conexão com serviço");
                Message response = Message.obtain();
                response.arg1 = ConfigActivity.MSG_FAILED;
                response.obj = new String("Falha ao conectar");
                sendMessageToActivity(response);
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

    class GamePlayerIncomingHandler extends GameService.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case MSG_CONNECT_TO_DEVICE:
                    String address = (String) msg.obj;
                    if (discoveredServices.containsKey(address)) {
                        WiFiP2pDiscoveredService service = discoveredServices.get(address);
                        Log.d(getTag(), "Conectando com " + service.getName());
                        connectP2p(service);
                    }
                    else {
                        Message response = Message.obtain();
                        response.arg1 = ConfigActivity.MSG_FAILED;
                        response.obj = new String("Servidor não encontrado");
                        sendMessageToActivity(response);
                    }
                    break;
                case MSG_REQUEST_DEVICES:
                    Message response = Message.obtain();
                    response.obj = discoveredServices;
                    response.arg1 = ClientConfigActivity.MSG_DEVICES;
                    sendMessageToActivity(response);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new GamePlayerIncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}