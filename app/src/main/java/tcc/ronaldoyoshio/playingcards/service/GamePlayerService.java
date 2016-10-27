package tcc.ronaldoyoshio.playingcards.service;

import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
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

import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.client.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.model.WebMessage;
import tcc.ronaldoyoshio.playingcards.model.web.WiFiP2pDiscoveredService;

public class GamePlayerService extends GameService {
    public static final int MSG_CONNECT_TO_DEVICE = 3;
    public static final int MSG_REQUEST_DEVICES = 4;

    private static final String TAG = "GamePlayerService";
    public static final String SERVICE_INSTANCE = "_gamePlayer";

    protected boolean connectingToDevice = false;
    private PlayerSocketHandler playerHandler;
    private WiFiP2pDiscoveredService serviceServer;

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    protected void startRegistration() {
        Map<String, String> record = new HashMap<>();
        record.put("NAME", name);

        service = WifiP2pDnsSdServiceInfo.newInstance(getServiceInstance(), SERVICE_REG_TYPE, record);
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
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

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
                sendToastMessage("Falha ao conectar com servidor.", ConfigActivity.MSG_TEXT);
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        cleanWifiP2P();
        playerHandler = new PlayerSocketHandler(p2pInfo.groupOwnerAddress, serviceServer.getPort());
        playerHandler.start();
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
                    sendToastMessage("Falha ao conectar com servidor.", ConfigActivity.MSG_TEXT);
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
                playerHandler.sendMessageServer(message);
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
                String message = (e.getMessage() != null) ? e.getMessage() : "";
                Log.d(TAG, message);
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

        public void sendMessageServer(WebMessage message) {
            try {
                output.writeObject(message);
            } catch (IOException e) {
                String eMessage = (e.getMessage() != null) ? e.getMessage() : "";
                Log.d(TAG, eMessage);
            }
        }

        public void finish() {
            if (socket != null && !socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                    String message = (e.getMessage() != null) ? e.getMessage() : "";
                    Log.d(TAG, message);
                    sendToastMessage("Falha na comunicação com servidor", ConfigActivity.MSG_ERROR);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (playerHandler != null && playerHandler.isAlive()) {
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
