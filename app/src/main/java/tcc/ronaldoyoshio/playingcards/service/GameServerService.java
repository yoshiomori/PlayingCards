package tcc.ronaldoyoshio.playingcards.service;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.activity.config.client.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.server.ServerConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.deck.DeckActivity;
import tcc.ronaldoyoshio.playingcards.activity.hand.HandActivity;
import tcc.ronaldoyoshio.playingcards.model.WebMessage;
import tcc.ronaldoyoshio.playingcards.model.web.server.WifiServer;

public class GameServerService extends GameService {
    public static final String SERVICE_INSTANCE = "_gameServer";
    private static final String TAG = "GameServerService";
    public static final int MSG_SERVER_SOCKET = 4;
    public static final int MSG_STOP_SOCKET = 5;
    private ServerSocketHandler server;
    protected Map<String, ClientHandler> clients = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
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

    class GameServerIncomingHandler extends GameService.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SERVER_SOCKET:
                    server = new ServerSocketHandler(4545);
                    server.start();
                    break;
                case MSG_STOP_SOCKET:
                    server.stopListening();
                    for (Map.Entry<String, ClientHandler> client : clients.entrySet()) {
                        WebMessage message = new WebMessage();
                        message.setTag(ClientConfigActivity.MSG_WEB_INIT);
                        client.getValue().sendMessageClient(message);
                    }
                    Message response = Message.obtain();
                    response.what = ServerConfigActivity.MSG_CONFIRM;
                    sendMessageToActivity(response);
                    stopLooking();
                    break;
                case MSG_SEND_CARD:
                    sendCardToPlayer(msg.getData().getString("Player"), msg.getData().getStringArrayList("Cards"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new GameServerIncomingHandler());

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

    private class ServerSocketHandler extends Thread {
        private ServerSocket serverSocket = null;
        private Integer serverPort;

        public ServerSocketHandler (Integer serverPort) {
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(serverPort);
                while (true) {
                    Socket playerSocket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(playerSocket);
                    client.start();
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                stopListening();
            }
        }

        public void stopListening() {
            try {
                if (serverSocket != null && !serverSocket.isClosed()){
                    serverSocket.close();
                }
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
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
                Log.d(getTag(), "aaa");
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                input = new ObjectInputStream(clientSocket.getInputStream());
                while (true) {
                    WebMessage message = (WebMessage) input.readObject();
                    handleMessage(message);
                }
            } catch (Exception e) {
                try {
                    clientSocket.close();
                } catch (IOException e1) {
                    Log.d(TAG, e.getMessage());
                }
                Log.d(TAG, e.getMessage());
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
                        for (Map.Entry<String, ClientHandler> client : clients.entrySet()) {
                            WebMessage webMessage = new WebMessage();
                            webMessage.setTag(ClientConfigActivity.MSG_WEB_PLAYER);
                            webMessage.insertMessage("Player", clientName);
                            client.getValue().sendMessageClient(webMessage);
                        }
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
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
