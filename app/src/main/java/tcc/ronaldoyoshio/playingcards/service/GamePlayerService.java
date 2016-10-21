package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
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
import tcc.ronaldoyoshio.playingcards.activity.deck.DeckActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.model.WebMessage;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public class GamePlayerService extends GameService {
    public static final int MSG_CONNECT_TO_DEVICE = 3;
    public static final int MSG_REQUEST_DEVICES = 4;

    private static final String TAG = "GamePlayerService";
    public static final String SERVICE_INSTANCE = "_gamePlayer";

    private PlayerSocketHandler handler = null;
    private WiFiP2pDiscoveredService serviceServer;

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    protected void startRegistration() {
        Map<String, String> record = new HashMap<>();
        record.put("NAME", name);

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(getServiceInstance(), SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(getTag(), "Serviço Local Adicionado");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(getTag(), "Falha ao adicionar o serviço: " + reason);
                sendToastMessage("Erro na inicialização WifiDirect. Tente Novamente", ConfigActivity.MSG_ERROR);
            }
        });
    }

    public void connectP2p(WiFiP2pDiscoveredService service) {
        serviceServer = service;
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
                        public void onFailure(int reason) {
                            Log.d(getTag(), "Falha ao remover serviceRequest: " + reason);
                            sendToastMessage("Erro na Conexão com Servidor. Tente Novamente", ConfigActivity.MSG_ERROR);
                        }
                    });

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(getTag(), "Conectando ao serviço");
                Message response = Message.obtain();
                response.what = ClientConfigActivity.MSG_CONNECT_OK;
                sendMessageToActivity(response);
                stopLooking();
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
                sendToastMessage("Falha ao conectar com servidor.", -1);
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        handler = new PlayerSocketHandler(p2pInfo.groupOwnerAddress, serviceServer.getPort());
        handler.start();
        stopLooking();
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
                    sendToastMessage("Falha ao conectar com servidor.", -1);
                }
                break;
            case MSG_REQUEST_DEVICES:
                for (WiFiP2pDiscoveredService service : discoveredServices.values()) {
                    sendDiscoveredServiceMessage(service);
                }
                break;
            case MSG_SEND_CARD:
                WebMessage message = new WebMessage();
                message.setTag(GameServerService.MSG_SEND_CARD);
                ArrayList<String> cards = msg.getData().getStringArrayList("Cards");
                for (int i = 0; i < cards.size(); i++) {
                    message.insertMessage("Card" + i, cards.get(i));
                }
                message.insertMessage("Player", msg.getData().getString("Player"));
                handler.sendMessageServer(message);
                break;
            default:
                super.handleMessage(msg);
        }
        return true;
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
                socket.connect(new InetSocketAddress(serverAddress.getHostAddress(), serverPort), 5000);
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                sendName();
                while (true) {
                    WebMessage message = (WebMessage) input.readObject();
                    handleMessage(message);
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                finish();
            }
        }

        private void sendName() {
            WebMessage message = new WebMessage();
            message.setTag(GameService.MSG_CLIENT);
            message.insertMessage("Nome", name);
            sendMessageServer(message);
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
                        if (players.equals("")) break;
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

        public void sendMessageServer(WebMessage message) {
            try {
                output.writeObject(message);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        public void finish() {
            if (socket != null && !socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                    sendToastMessage("Falha na comunicação com servidor", ConfigActivity.MSG_TEXT);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (handler != null && handler.isAlive()) {
            handler.interrupt();
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
