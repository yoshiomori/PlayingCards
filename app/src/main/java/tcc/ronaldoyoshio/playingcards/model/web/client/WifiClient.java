package tcc.ronaldoyoshio.playingcards.model.web.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class WifiClient implements Runnable {

    private Socket socket = null;
    private int serverPort;
    private InetAddress serverAddress;

    public WifiClient (int port) {
        this.serverPort = port;
    }

    @Override
    public void run() {
        socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(serverAddress, serverPort), 5000);
            waitMessages();
        } catch (IOException e) {
            // aaa
        }
    }

    private void waitMessages() {

    }
}
