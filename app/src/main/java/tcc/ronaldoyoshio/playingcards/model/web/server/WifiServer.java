package tcc.ronaldoyoshio.playingcards.model.web.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WifiServer implements ServerInterface {

    private static WifiServer server = new WifiServer();
    private ServerSocket serverSocket;
    private final int serverPort = 8888;

    private WifiServer() {}

    public static WifiServer getInstance() {
        return server;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(getPort());
            Socket socket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Não foi possível iniciar servidor.");
        }
    }

    public int getPort() {
        return serverPort;
    }

    @Override
    public void closeServer() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Não foi possível encerrar servidor.");
            }
        }
    }
}
