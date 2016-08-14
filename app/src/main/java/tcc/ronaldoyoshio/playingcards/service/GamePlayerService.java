package tcc.ronaldoyoshio.playingcards.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class GamePlayerService extends Service {

    public static final String CLIENT_ACTION = "tcc.ronaldoyoshio.playingcards.CLIENT_ACTION";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
