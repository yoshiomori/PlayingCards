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

import tcc.ronaldoyoshio.playingcards.model.WebMessage;
import tcc.ronaldoyoshio.playingcards.model.web.server.WifiServer;

public class GameServerService extends GameService {
    public static final String SERVICE_INSTANCE = "_gameServer";
    private static final String TAG = "GameServerService";
    private String name = "Server";

    protected WifiServer wifiServer = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    class GameServerIncomingHandler extends GameService.IncomingHandler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    final Messenger mMessenger = new Messenger(new GameServerIncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread server = new ServerSocketHandler(4545);
        server.start();
    }

    class ServerSocketHandler extends Thread {
        private ServerSocket serverSocket = null;
        private Integer serverPort;
        private boolean listeningClients = true;
        private ObjectInputStream input;
        private ObjectOutputStream output;

        public ServerSocketHandler (Integer serverPort) {
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(serverPort);
                //while (listeningClients) {
                Log.d(TAG, "Vamos gentar");
                Socket playerSocket = serverSocket.accept();
                Log.d(TAG, "qqqqqq");
                output = new ObjectOutputStream(playerSocket.getOutputStream());
                Log.d(TAG, "qqqqqq");
                input = new ObjectInputStream(playerSocket.getInputStream());
                Log.d(TAG, "qqqqqq");
                WebMessage response = new WebMessage();
                Log.d(TAG, "qqqqqq");
                response.insertMessage("A", "BSD");
                Log.d(TAG, "qqqqqq");
                response.insertMessage("B", "AAA");
                sendMessagePlayer(response);
                Log.d(TAG, "qqqqqq");
                while (true) {
                    Log.d(TAG, "Vamos EEEE");
                    WebMessage message = (WebMessage) input.readObject();
                    Log.d(TAG, message.getMessage("S"));
                }
                //}
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                try {
                    if (serverSocket != null && !serverSocket.isClosed()){
                        serverSocket.close();
                    }
                } catch (IOException e1) {
                    Log.d(TAG, e.getMessage());
                }
            }

        }

        public void sendMessagePlayer(WebMessage message) {
            try {
                output.writeObject(message);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        public void stopListening() {
            this.listeningClients = false;
        }
    }
}
