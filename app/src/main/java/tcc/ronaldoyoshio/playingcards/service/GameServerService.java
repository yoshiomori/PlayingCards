package tcc.ronaldoyoshio.playingcards.service;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

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

        System.out.println("aaaa");
        if (p2pInfo.isGroupOwner) {
            System.out.println("aaad");
            Log.d(getTag(), p2pInfo.groupOwnerAddress.toString());
        } else {
            System.out.println("aas");
            Log.d(getTag(), "Nao chefe");
        }
    }
}
