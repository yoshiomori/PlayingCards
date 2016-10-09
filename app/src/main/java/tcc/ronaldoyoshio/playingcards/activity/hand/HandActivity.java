package tcc.ronaldoyoshio.playingcards.activity.hand;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;
import tcc.ronaldoyoshio.playingcards.service.GameService;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.OnSendCard;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.SendCard;

public class HandActivity extends GLActivity {
    MotionCardImage motionCardImage;
    public static final int MSG_RECEIVE_CARD = 1 ;
    protected boolean mBound = false;
    protected Messenger mService = null;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private OnSendCard sendCardEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<String> playersName = null;
        ArrayList<Integer> directions = null;

        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGroundImage());

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("playersName")) {
            /* Recuperando a configuração do TouchConfigActivity, activity anterior */
                playersName = extras.getStringArrayList("playersName");
            }
            if (extras.containsKey("directions")) {
                directions = extras.getIntegerArrayList("directions");
            }

            motionCardImage = new MotionCardImage(this);
            sendCardEvent = new SendCard(motionCardImage, playersName, directions, mService);
            motionCardImage.setOnSendCard(sendCardEvent);
            addImage(motionCardImage);
        }

        super.onCreate(savedInstanceState);
        bindService(new Intent(this, GamePlayerService.class), mConnection, 0);
    }

    public void onReceiveCard(ArrayList<String> cards) {
        Log.d("Hand", String.valueOf(cards.size()));
        for (String card :
                cards) {
            motionCardImage.addCard(card);
        }
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            ((SendCard) sendCardEvent).setmService(mService);
            mBound = true;
            ((SendCard) sendCardEvent).setmBound(mBound);
            Message msg = Message.obtain();
            msg.what = GameService.MSG_CLIENT;
            msg.arg1 = 1;
            msg.replyTo = mMessenger;
            sendMessageToService(msg);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECEIVE_CARD:
                    onReceiveCard(msg.getData().getStringArrayList("Cards"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void sendMessageToService(Message msg) {
        if (!mBound) return;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
