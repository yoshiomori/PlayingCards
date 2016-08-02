package tcc.ronaldoyoshio.playingcards.model.game;

import java.util.ArrayList;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.model.web.server.ServerInterface;

public class GameServer {
    private List<GamePlayer> players = new ArrayList<>();
    private ServerInterface webServer;

    public ServerInterface getWebServer() {
        return webServer;
    }

    public void setWebServer(ServerInterface webServer) {
        this.webServer = webServer;
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players = players;
    }

    private GameServer(ServerInterface webServer) {
        this.webServer = webServer;
    }

}
