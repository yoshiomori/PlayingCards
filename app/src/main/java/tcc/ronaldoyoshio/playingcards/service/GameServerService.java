package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.model.web.server.ServerInterface;

public class GameServerService extends Service {

    public static final String SERVER_ACTION = "tcc.ronaldoyoshio.playingcards.SERVER_ACTION";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
