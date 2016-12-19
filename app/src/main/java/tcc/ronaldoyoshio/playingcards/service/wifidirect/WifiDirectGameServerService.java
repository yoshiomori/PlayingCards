package tcc.ronaldoyoshio.playingcards.service.wifidirect;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.activity.config.AbstractConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.ServerConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.deck.DeckActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.model.WebMessage;

public class WifiDirectGameServerService extends AbstractWifiDirectGameService {
    private static final String TAG = "WifiGameServerService";
    public static final String SERVICE_INSTANCE = "_gameServer";
    public static final int MSG_STOP_SOCKET = 3;

    private ServerSocketHandler server;
    private WifiP2pDnsSdServiceInfo service;
    private Map<String, ClientHandler> clients = new HashMap<>();

    @Override
    protected String getServiceInstance() {
        return this.SERVICE_INSTANCE;
    }

    @Override
    protected String getTag() {
        return this.TAG;
    }

    protected void startWifiP2p() {
        Map<String, String> record = new HashMap<>();
        server = new ServerSocketHandler();
        server.start();
        while(server.getServerSocketPort() == null);
        record.put("LISTEN_PORT", String.valueOf(server.getServerSocketPort()));
        record.put("NAME", name);

        service = WifiP2pDnsSdServiceInfo.newInstance(getServiceInstance(), SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(getTag(), "Serviço Local Adicionado");
                manager.createGroup(channel, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(getTag(), "Grupo criado com sucesso.");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(getTag(), "Falha ao adicionar o serviço: " + reason);
                        sendToastMessage("Erro na inicialização WifiDirect. Tente Novamente", AbstractConfigActivity.MSG_ERROR);
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                Log.d(getTag(), "Falha ao adicionar o serviço: " + reason);
                sendToastMessage("Erro na inicialização WifiDirect. Tente Novamente", AbstractConfigActivity.MSG_ERROR);
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_STOP_SOCKET:
                for (Map.Entry<String, ClientHandler> client : clients.entrySet()) {
                    WebMessage message = new WebMessage();
                    message.setTag(ClientConfigActivity.MSG_WEB_INIT);
                    Integer playerIndex = 0;
                    for (Map.Entry<String, ClientHandler> player : clients.entrySet()) {
                        if (!client.getKey().equals(player.getKey())) {
                            message.insertMessage("Player" + playerIndex, player.getKey());
                            playerIndex++;
                        }
                    }
                    client.getValue().sendMessageClient(message);
                }
                Message response = Message.obtain();
                response.what = ServerConfigActivity.MSG_CONFIRM;
                sendMessageToActivity(response);
                server.stopListening();
                cleanWifiP2P();
                break;
            case MSG_SEND_CARD:
                sendCardsToPlayer(msg.getData().getString("Player"), msg.getData().getStringArrayList("Cards"), msg.getData().getBooleanArray("upsidedown"));
                break;
            default:
                super.handleMessage(msg);
        }
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        if (p2pInfo.isGroupOwner) {
            Log.d(getTag(), "Novo Cliente conectando");
        }
    }

    private void sendCardsToPlayer(String player, ArrayList<String> cards, boolean[] upsidedowns) {
        WebMessage message = new WebMessage();
        message.setTag(HandActivity.MSG_RECEIVE_CARD);
        for (int i = 0; i < cards.size(); i++) {
            message.insertMessage("Card" + i, cards.get(i));
            message.insertMessage("upsidedown" + i, String.valueOf(upsidedowns[i]));
        }
        clients.get(player).sendMessageClient(message);
    }

    @Override
    protected void cleanWifiP2P() {
        if (manager != null && channel != null && service != null) {
            manager.removeLocalService(channel, service, new ActionListener() {
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
    }

    @Override
    public void onDestroy() {
        for (Map.Entry<String, ClientHandler> entry: clients.entrySet()) {
            if (entry.getValue().isAlive()) {
                entry.getValue().finish();
                entry.getValue().interrupt();
            }
        }
        if (server != null && server.isAlive()) {
            server.stopListening();
            server.interrupt();
        }
        super.onDestroy();
    }

    private class ServerSocketHandler extends Thread {
        private ServerSocket serverSocket = null;

        public ServerSocketHandler () {
            try {
                serverSocket = new ServerSocket(0);
            } catch (IOException e) {
                String message = (e.getMessage() != null) ? e.getMessage() : "1";
                Log.d(TAG, message);
                sendToastMessage("Falha na criação do Socket", AbstractConfigActivity.MSG_ERROR);
            }
        }

        public Integer getServerSocketPort() {
            return (serverSocket != null) ? serverSocket.getLocalPort() : null;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Socket playerSocket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(playerSocket);
                    client.start();
                }
            } catch (Exception e) {
                String message = (e.getMessage() != null) ? e.getMessage() : "2";
                Log.d(TAG, message + " - Falhar na criação ClientHandler ou ServerSocket parando");
                stopListening();
            }
        }

        public void stopListening() {
            try {
                if (serverSocket != null && !serverSocket.isClosed()){
                    serverSocket.close();
                }
            } catch (IOException e) {
                String message = (e.getMessage() != null) ? e.getMessage() : "3";
                Log.d(TAG, message);
                sendToastMessage("Falha ao fechar socket.", AbstractConfigActivity.MSG_ERROR);
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket = null;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                input = new ObjectInputStream(clientSocket.getInputStream());
                while (true) {
                    WebMessage message = (WebMessage) input.readObject();
                    handleMessage(message);
                }
            } catch (Exception e) {
                finish();
                sendToastMessage("Falha na comunicação com Jogador", AbstractConfigActivity.MSG_ERROR);
            }
        }

        private void handleMessage(WebMessage message) {
            switch (message.getTag()) {
                case MSG_SEND_CARD:
                    String player = message.getMessage("Player");
                    ArrayList<String> cards = new ArrayList<>();
                    ArrayList<Boolean> upsidedownList = new ArrayList<>();
                    for (int i = 0; true; i++) {
                        String card = message.getMessage("Card" + i);
                        boolean bool = Boolean.parseBoolean(message.getMessage("upsidedown" + i));
                        if (card.equals("")) break;
                        else {
                            cards.add(card);
                            upsidedownList.add(bool);
                        }
                    }
                    boolean[] upsidedown = new boolean[upsidedownList.size()];

                    for (int i = 0; i < upsidedownList.size(); i++) {
                        upsidedown[i] = upsidedownList.get(i);
                    }

                    if (player.equals(name)) {
                        Message response = Message.obtain();
                        response.what = DeckActivity.MSG_RECEIVE_CARD;
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("Cards", cards);
                        bundle.putBooleanArray("upsidedown", upsidedown);
                        response.setData(bundle);
                        sendMessageToActivity(response);
                    }
                    else {
                        sendCardsToPlayer(player, cards, upsidedown);
                    }
                    String toastMessage = clientName + " enviou " + cards.size() + " carta(s) para " + player;
                    sendToastMessage(toastMessage, AbstractConfigActivity.MSG_TEXT);
                    break;
                case MSG_CLIENT:
                    clientName = message.getMessage("Nome");
                    if (!clients.containsKey(clientName)) {
                        clients.put(clientName, this);
                        Message msg = Message.obtain();
                        msg.what = ServerConfigActivity.MSG_NEW_DEVICE;
                        Bundle bundle = new Bundle();
                        bundle.putString("Nome", clientName);
                        msg.setData(bundle);
                        sendMessageToActivity(msg);
                    }
                    break;
                default:
                    break;
            }
        }

        public synchronized void sendMessageClient(WebMessage message) {
            try {
                output.writeObject(message);
            } catch (IOException e) {
                String eMessage = (e.getMessage() != null) ? e.getMessage() : "4";
                Log.d(TAG, eMessage);
                finish();
                sendToastMessage("Falha na comunicação com Jogador", AbstractConfigActivity.MSG_ERROR);
            }
        }

        public void finish() {
            if (clientSocket != null && !clientSocket.isClosed()){
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    String message = (e.getMessage() != null) ? e.getMessage() : "5";
                    Log.d(TAG, message);
                    sendToastMessage("Falha na comunicação com Jogador", AbstractConfigActivity.MSG_ERROR);
                }
            }
        }
    }
}
