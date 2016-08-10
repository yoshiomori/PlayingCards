package tcc.ronaldoyoshio.playingcards.BroadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;

public class GameBroadcastReceiver extends BroadcastReceiver {
    private Activity activity;

    public GameBroadcastReceiver (Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(GameServerService.SERVER_ACTION)) {

        }

        else if (action.equals(GamePlayerService.CLIENT_ACTION)) {

        }
    }
}
