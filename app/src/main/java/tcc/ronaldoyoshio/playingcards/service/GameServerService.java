package tcc.ronaldoyoshio.playingcards.service;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.model.web.server.ServerInterface;

public class GameServerService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GameServerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
