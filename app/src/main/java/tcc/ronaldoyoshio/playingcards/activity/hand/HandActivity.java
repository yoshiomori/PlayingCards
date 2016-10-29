package tcc.ronaldoyoshio.playingcards.activity.hand;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.application.PlayingCardsApplication;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;
import tcc.ronaldoyoshio.playingcards.service.GameService;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.OnSendCard;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.SendCard;

public class HandActivity extends GLActivity implements Handler.Callback {
    private static final String TAG = "HandActivity";
    public static final int MSG_TEXT = 3;
    public static final int MSG_RECEIVE_CARD = 4;
    public static final int MSG_ERROR = 5;

    private MotionCardImage motionCardImage;
    private boolean mBound = false;
    private Messenger mService = null;
    private final Handler handler = new Handler(this);
    private final Messenger mMessenger = new Messenger(handler);
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

    private void onReceiveCard(ArrayList<String> cards) {
        for (String card :
                cards) {
            motionCardImage.addCard(card);
        }
        getScreen().requestRender();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            setServiceSendCard(mService, true);
            Message msg = Message.obtain();
            msg.what = GameService.MSG_CLIENT;
            msg.arg1 = 1;
            msg.replyTo = mMessenger;
            sendMessageToService(msg);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
            setServiceSendCard(null, false);
        }

        private void setServiceSendCard (Messenger service, boolean bound) {
            ((SendCard) sendCardEvent).setmService(service);
            ((SendCard) sendCardEvent).setmBound(bound);
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RECEIVE_CARD:
                onReceiveCard(msg.getData().getStringArrayList("Cards"));
                break;
            case MSG_TEXT:
                String message = (msg.getData().getString("Mensagem") != null) ? msg.getData().getString("Mensagem") : "";
                Log.d(TAG, message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if (msg.arg1 == MSG_ERROR) finish();
                break;
        }
        return true;
    }

    private void sendMessageToService(Message msg) {
        if (!mBound) return;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        if (mBound) {
            unbindService(mConnection);
        }
        handler.removeCallbacks(null);
        PlayingCardsApplication.getInstance().stopServices();
        super.onDestroy();
    }
}
