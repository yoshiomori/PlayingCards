package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.client.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.model.WebMessage;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public class GamePlayerService extends GameService {
    public static final int MSG_CONNECT_TO_DEVICE = 4;
    public static final int MSG_REQUEST_DEVICES = 5;
    public static final String SERVICE_INSTANCE = "_gamePlayer";
    private static final String TAG = "GamePlayerService";

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
                response.arg1 = ClientConfigActivity.MSG_TEXT;
                Bundle bundle = new Bundle();
                bundle.putString("Mensagem", "Conectado com Servidor");
                response.setData(bundle);
                sendMessageToActivity(response);
            }

            @Override
            public void onFailure(int errorCode) {
                Log.d(getTag(), "Falha na conexão com serviço");
                Message response = Message.obtain();
                response.arg1 = ClientConfigActivity.MSG_CONNECT_NOK;
                Bundle bundle = new Bundle();
                bundle.putString("Mensagem", "Falha ao conectar");
                response.setData(bundle);
                sendMessageToActivity(response);
            }
        });
    }

    @Override
    protected String getServiceInstance() {
        return this.SERVICE_INSTANCE;
    }

    @Override
    protected String getTag() {
        return this.TAG;
    }

    final Messenger mMessenger = new Messenger(new GamePlayerIncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread handler = new PlayerSocketHandler(p2pInfo.groupOwnerAddress, 4545);
        handler.start();
    }

    private class GamePlayerIncomingHandler extends GameService.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            Message response;
            switch (msg.arg1) {
                case MSG_CONNECT_TO_DEVICE:
                    String address = msg.getData().getString("Address");
                    if (discoveredServices.containsKey(address)) {
                        WiFiP2pDiscoveredService service = discoveredServices.get(address);
                        Log.d(getTag(), "Conectando com " + service.getName());
                        connectP2p(service);
                    }
                    else {
                        response = Message.obtain();
                        response.arg1 = ClientConfigActivity.MSG_CONNECT_NOK;
                        Bundle bundle = new Bundle();
                        bundle.putString("Mensagem", "Servidor não encontrado");
                        response.setData(bundle);
                        sendMessageToActivity(response);
                    }
                    break;
                case MSG_REQUEST_DEVICES:
                    for (WiFiP2pDiscoveredService service : discoveredServices.values()) {
                        sendDiscoveredServiceMessage(service);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    class PlayerSocketHandler extends Thread {
        private Socket socket = null;
        private InetAddress serverAddress;
        private Integer serverPort;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        public PlayerSocketHandler (InetAddress serverAddress, Integer serverPort) {
            this.socket = new Socket();
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
        }
        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(serverAddress.getHostAddress(), serverPort), 10000);
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                while (true) {
                    WebMessage message = (WebMessage) input.readObject();
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                    try {
                        if (socket != null && !socket.isClosed()){
                            socket.close();
                        }
                    } catch (IOException e1) {
                        Log.d(TAG, e.getMessage());
                    }
            }
        }

        public void sendMessageServer(WebMessage message) {
            try {
                output.writeObject(message);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
