package tcc.ronaldoyoshio.playingcards.service;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
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
import java.util.HashMap;
import java.util.Map;

import tcc.ronaldoyoshio.playingcards.model.WebMessage;
import tcc.ronaldoyoshio.playingcards.model.web.server.WifiServer;

public class GameServerService extends GameService {
    public static final String SERVICE_INSTANCE = "_gameServer";
    private static final String TAG = "GameServerService";
    private static final int MSG_SERVER_SOCKET = 4;
    private ServerSocketHandler server;
    protected WifiServer wifiServer = null;

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
            switch (msg.arg1) {
                case MSG_SERVER_SOCKET:
                    server = new ServerSocketHandler(4545);
                    server.start();
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
        if (!clients.containsKey(p2pInfo.groupOwnerAddress.getHostAddress())) {
            clients.put(p2pInfo.groupOwnerAddress.getHostAddress(), null);
        }
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
                    if (clients.containsKey(playerSocket.getInetAddress().getHostAddress())) {
                        ClientHandler client = new ClientHandler(playerSocket);
                        clients.put(playerSocket.getInetAddress().getHostAddress(), client);
                        client.start();
                    }
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
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                input = new ObjectInputStream(clientSocket.getInputStream());
                while (true) {
                    WebMessage message = (WebMessage) input.readObject();
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
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
