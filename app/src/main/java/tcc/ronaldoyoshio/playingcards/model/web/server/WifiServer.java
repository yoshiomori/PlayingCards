package tcc.ronaldoyoshio.playingcards.model.web.server;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;

public class WifiServer {
    ServerSocket socket = null;
    private Handler handler;
    private static final String TAG = "WifiServer";
    private int port = 8888;
    private InputStream iStream;
    private OutputStream oStream;

    public WifiServer(Handler handler) throws IOException {
        socket = new ServerSocket(port);
        this.handler = handler;
        Log.d(TAG, "Server socket iniciado");
    }

    /*public void run() {
        while (true) {
            try {


            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                break;
            }
        }
    }*/
}
