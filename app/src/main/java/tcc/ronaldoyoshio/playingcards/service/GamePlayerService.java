package tcc.ronaldoyoshio.playingcards.service;

import android.app.IntentService;
import android.content.Intent;

public class GamePlayerService extends IntentService {

    public static final String CLIENT_ACTION = "tcc.ronaldoyoshio.playingcards.CLIENT_ACTION";

    public GamePlayerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
