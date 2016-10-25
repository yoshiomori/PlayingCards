package tcc.ronaldoyoshio.playingcards.service;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pGroup;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.activity.config.ConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.client.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.server.ServerConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.deck.DeckActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.model.WebMessage;

public class GameServerService extends GameService {
    public static final int MSG_STOP_SOCKET = 3;

    private static final String TAG = "GameServerService";
    public static final String SERVICE_INSTANCE = "_gameServer";

    private ServerSocketHandler server;
    protected Map<String, ClientHandler> clients = new HashMap<>();

    @Override
    protected String getServiceInstance() {
        return this.SERVICE_INSTANCE;
    }

    @Override
    protected String getTag() {
        return this.TAG;
    }

    protected void startRegistration() {
        Map<String, String> record = new HashMap<>();
        record.put("LISTEN_PORT", String.valueOf(startServerSocket()));
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

    private Integer startServerSocket() {
        server = new ServerSocketHandler();
        server.start();
        return server.getServerSocketPort();
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
                sendCardToPlayer(msg.getData().getString("Player"), msg.getData().getStringArrayList("Cards"));
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
        Log.d(getTag(), "Novo Cliente conectando");
    }

    private void sendCardToPlayer(String player, ArrayList<String> cards) {
        WebMessage message = new WebMessage();
        message.setTag(HandActivity.MSG_RECEIVE_CARD);
        for (int i = 0; i < cards.size(); i++) {
            message.insertMessage("Card" + i, cards.get(i));
        }
        clients.get(player).sendMessageClient(message);
    }

    private void disconnect() {
        if (manager != null && channel != null) {
            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && manager != null && channel != null
                            && group.isGroupOwner()) {
                        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
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

    @Override
    public void onDestroy() {
        for (Map.Entry<String, ClientHandler> entry: clients.entrySet()) {
            if (entry.getValue().isAlive()) {
                entry.getValue().interrupt();
            }
        }
        if (server != null && server.isAlive()) {
            server.interrupt();
        }
        disconnect();
        super.onDestroy();
    }

    private class ServerSocketHandler extends Thread {
        private ServerSocket serverSocket = null;

        public ServerSocketHandler () {
            try {
                serverSocket = new ServerSocket(0);
            } catch (IOException e) {
                String message = (e.getMessage() != null) ? e.getMessage() : "";
                Log.d(TAG, message);
                sendToastMessage("Falha na criação do Socket", ConfigActivity.MSG_ERROR);
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
                String message = (e.getMessage() != null) ? e.getMessage() : "";
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
                String message = (e.getMessage() != null) ? e.getMessage() : "";
                Log.d(TAG, message);
                sendToastMessage("Falha ao fechar socket.", ConfigActivity.MSG_ERROR);
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket = null;
        private ObjectInputStream input;
        private ObjectOutputStream output;

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
            }
        }

        private void handleMessage(WebMessage message) {
            switch (message.getTag()) {
                case MSG_SEND_CARD:
                    String player = message.getMessage("Player");
                    ArrayList<String> cards = new ArrayList<>();
                    for (int i = 0; true; i++) {
                        String card = message.getMessage("Card" + i);
                        if (card.equals("")) break;
                        else cards.add(card);
                    }

                    if (player.equals(name)) {
                        Message response = Message.obtain();
                        response.what = DeckActivity.MSG_RECEIVE_CARD;
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("Cards", cards);
                        response.setData(bundle);
                        sendMessageToActivity(response);
                    }
                    else {
                        sendCardToPlayer(player, cards);
                    }
                    break;
                case MSG_CLIENT:
                    String clientName = message.getMessage("Nome");
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

        public void sendMessageClient(WebMessage message) {
            try {
                output.writeObject(message);
            } catch (IOException e) {
                String eMessage = (e.getMessage() != null) ? e.getMessage() : "";
                Log.d(TAG, eMessage);
                finish();
            }
        }

        public void finish() {
            if (clientSocket != null && !clientSocket.isClosed()){
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    String message = (e.getMessage() != null) ? e.getMessage() : "";
                    Log.d(TAG, message);
                    sendToastMessage("Falha na comunicação com Jogador", ConfigActivity.MSG_ERROR);
                }
            }
        }
    }
}
