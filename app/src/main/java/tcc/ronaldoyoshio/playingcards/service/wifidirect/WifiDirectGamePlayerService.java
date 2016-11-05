package tcc.ronaldoyoshio.playingcards.service.wifidirect;

import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.activity.config.AbstractConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.model.WebMessage;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public class WifiDirectGamePlayerService extends AbstractWifiDirectGameService {
    public static final int MSG_CONNECT_TO_DEVICE = 3;
    public static final int MSG_REQUEST_DEVICES = 4;

    private static final String TAG = "WifiGamePlayerService";
    public static final String SERVICE_INSTANCE = "_gamePlayer";

    private PlayerSocketHandler playerHandler;
    private WiFiP2pDiscoveredService serviceServer;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private Map<String, WiFiP2pDiscoveredService> discoveredServices = new HashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    protected void startWifiP2p() {
        DnsSdTxtRecordListener txtListener = new DnsSdTxtRecordListener() {
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

        DnsSdServiceResponseListener servListener = new DnsSdServiceResponseListener() {
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
        manager.addServiceRequest(channel, serviceRequest, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(getTag(), "Requisição adicionado com sucesso");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(getTag(), "Requisição adicionado sem sucesso: " + reason);
                sendToastMessage("Erro na inicialização WifiDirect. Tente Novamente", AbstractConfigActivity.MSG_ERROR);
            }
        });

        manager.discoverServices(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(getTag(), "Iniciando procura de serviços");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(getTag(), "Falha na procura de serviços: " + reason);
                sendToastMessage("Erro na inicialização WifiDirect. Tente Novamente", AbstractConfigActivity.MSG_ERROR);
            }
        });
    }

    private void connectP2p(WiFiP2pDiscoveredService service) {
        serviceServer = service;
        WifiP2pConfig config = new WifiP2pConfig();
        config.groupOwnerIntent = 0;
        config.wps.setup = WpsInfo.PBC;
        config.deviceAddress = service.getDevice().deviceAddress;
        manager.connect(channel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(getTag(), "Conectando ao serviço");
                Message response = Message.obtain();
                response.what = ClientConfigActivity.MSG_CONNECT_OK;
                sendMessageToActivity(response);
            }

            @Override
            public void onFailure(int reason) {
                Log.d(getTag(), "Falha na conexão com serviço: " + reason);
                Message response = Message.obtain();
                response.what = ClientConfigActivity.MSG_CONNECT_NOK;
                Bundle bundle = new Bundle();
                bundle.putString("Mensagem", "Falha ao conectar");
                response.setData(bundle);
                sendMessageToActivity(response);
                sendToastMessage("Falha ao conectar com servidor.", AbstractConfigActivity.MSG_TEXT);
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        if (serviceServer != null && !p2pInfo.isGroupOwner) {
            playerHandler = new PlayerSocketHandler(p2pInfo.groupOwnerAddress, serviceServer.getPort());
            playerHandler.start();
            cleanWifiP2P();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        Message response;
        switch (msg.what) {
            case MSG_CONNECT_TO_DEVICE:
                String address = msg.getData().getString("Address");
                if (discoveredServices.containsKey(address)) {
                    WiFiP2pDiscoveredService service = discoveredServices.get(address);
                    Log.d(getTag(), "Conectando com " + service.getName());
                    connectP2p(service);
                }
                else {
                    response = Message.obtain();
                    response.what = ClientConfigActivity.MSG_CONNECT_NOK;
                    Bundle bundle = new Bundle();
                    bundle.putString("Mensagem", "Servidor não encontrado");
                    response.setData(bundle);
                    sendMessageToActivity(response);
                    sendToastMessage("Falha ao conectar com servidor.", AbstractConfigActivity.MSG_TEXT);
                }
                break;
            case MSG_REQUEST_DEVICES:
                for (WiFiP2pDiscoveredService service : discoveredServices.values()) {
                    sendDiscoveredServiceMessage(service);
                }
                break;
            case MSG_SEND_CARD:
                WebMessage message = new WebMessage();
                message.setTag(MSG_SEND_CARD);
                ArrayList<String> cards = msg.getData().getStringArrayList("Cards");
                for (int i = 0; cards != null && i < cards.size(); i++) {
                    message.insertMessage("Card" + i, cards.get(i));
                }
                message.insertMessage("Player", msg.getData().getString("Player"));
                playerHandler.sendMessageServer(message);
                break;
            default:
                super.handleMessage(msg);
        }
        return true;
    }

    private void sendDiscoveredServiceMessage(WiFiP2pDiscoveredService service) {
        Message msg = Message.obtain();
        msg.what = ClientConfigActivity.MSG_NEW_DEVICE;
        Bundle bundle = new Bundle();
        bundle.putParcelable("Device", service);
        msg.setData(bundle);
        sendMessageToActivity(msg);
    }

    @Override
    protected void cleanWifiP2P() {
        if (manager != null && channel != null) {
            manager.stopPeerDiscovery(channel, new ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(getTag(), "Finalizando a procura de dispositivos");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(getTag(), "Falha na finalizacao de procura de disposiivos: " + reason);
                }
            });

            manager.clearLocalServices(channel, new ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(getTag(), "Limpando Servicos Locais");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(getTag(), "Falha na Limpeza de Servicos: " + reason);
                }
            });

            manager.clearServiceRequests(channel, new ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(getTag(), "Limpando Requisições");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(getTag(), "Falha na Limpeza de Requisições: " + reason);
                }
            });

            if (serviceRequest != null) {
                manager.removeServiceRequest(channel, serviceRequest, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(getTag(), "Service Request removido");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(getTag(), "Falha ao remover serviceRequest: " + reason);
                        sendToastMessage("Erro na Conexão com Servidor. Tente Novamente", AbstractConfigActivity.MSG_ERROR);
                    }
                });
            }
        }
    }

    private class PlayerSocketHandler extends Thread {
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
                socket.connect(new InetSocketAddress(serverAddress.getHostAddress(), serverPort), 5000);
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                WebMessage nameMessage = new WebMessage();
                nameMessage.setTag(MSG_CLIENT);
                nameMessage.insertMessage("Nome", name);
                sendMessageServer(nameMessage);
                while (true) {
                    WebMessage message = (WebMessage) input.readObject();
                    handleMessage(message);
                }
            } catch (Exception e) {
                String message = (e.getMessage() != null) ? e.getMessage() : "1";
                Log.d(TAG, message);
                finish();
                sendToastMessage("Falha na comunicação com servidor", AbstractConfigActivity.MSG_ERROR);
            }
        }

        private void handleMessage(WebMessage message) {
            Message msg = Message.obtain();
            msg.what = message.getTag();
            Bundle bundle = new Bundle();

            switch (msg.what) {
                case ClientConfigActivity.MSG_WEB_INIT:
                    ArrayList<String> players = new ArrayList<>();
                    for (int i = 0; true; i++) {
                        String player = message.getMessage("Player" + i);
                        if (player.equals("")) break;
                        else players.add(player);
                    }
                    bundle.putStringArrayList("Players", players);
                    break;
                case HandActivity.MSG_RECEIVE_CARD:
                    ArrayList<String> cards = new ArrayList<>();
                    for (int i = 0; true; i++) {
                        String card = message.getMessage("Card" + i);
                        if (card.equals("")) break;
                        else cards.add(card);
                    }
                    bundle.putStringArrayList("Cards", cards);
                    break;
                default:
                    break;
            }
            msg.setData(bundle);
            sendMessageToActivity(msg);
        }

        public synchronized void sendMessageServer(WebMessage message) {
            try {
                output.writeObject(message);
            } catch (IOException e) {
                String eMessage = (e.getMessage() != null) ? e.getMessage() : "2";
                Log.d(TAG, eMessage);
                finish();
                sendToastMessage("Falha na comunicação com servidor", AbstractConfigActivity.MSG_ERROR);
            }
        }

        public void finish() {
            if (socket != null && !socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                    String message = (e.getMessage() != null) ? e.getMessage() : "3";
                    Log.d(TAG, message);
                    sendToastMessage("Falha na comunicação com servidor", AbstractConfigActivity.MSG_ERROR);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (playerHandler != null && playerHandler.isAlive()) {
            playerHandler.finish();
            playerHandler.interrupt();
        }
        super.onDestroy();
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
