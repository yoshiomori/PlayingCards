package tcc.ronaldoyoshio.playingcards.service;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.model.web.server.ServerInterface;

public class GameServerService extends IntentService {

    public static final String SERVER_ACTION = "tcc.ronaldoyoshio.playingcards.SERVER_ACTION";

    public GameServerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
