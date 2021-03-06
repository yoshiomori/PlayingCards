package tcc.ronaldoyoshio.playingcards.activity.deck;

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
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.application.PlayingCardsApplication;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.images.CardImage;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.model.Cards;
import tcc.ronaldoyoshio.playingcards.service.wifidirect.AbstractWifiDirectGameService;
import tcc.ronaldoyoshio.playingcards.service.wifidirect.WifiDirectGameServerService;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.OnSendCard;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.SendCard;

public class DeckActivity extends GLActivity implements Handler.Callback {
    private static final String TAG = "DeckActivity";
    public static final int MSG_TEXT = 3;
    public static final int MSG_RECEIVE_CARD = 4;
    public static final int MSG_ERROR = 5;
    private Cards cards;
    private ArrayList<String> playersName;
    private ArrayList<Integer> directions;
    private MotionCardImage cardImage;
    private OnSendCard sendCardEvent;

    private boolean mBound = false;
    private Messenger mService = null;
    private final Handler handler = new Handler(this);
    private final Messenger mMessenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
            if (extras.containsKey("cards")) {
                cards = new Cards(extras.getStringArrayList("cards"));
            }
            else {
                cards = new Cards();
            }
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("cards")) {
                cards = new Cards(savedInstanceState.getStringArrayList("cards"));
            }
        }

        cardImage = new MotionCardImage(this);

        sendCardEvent = new SendCard(cardImage, playersName, directions, mService);
        cardImage.setOnSendCard(sendCardEvent);
        addImage(cardImage);

        super.onCreate(savedInstanceState);

        print(cards);
        bindService(new Intent(this, WifiDirectGameServerService.class), mConnection, 0);
    }

    private void print(Cards cards) {
        cardImage.setCards(cards);
        cardImage.setTotalCards(cards.size());
        cardImage.setMode(CardImage.CENTERED);
        getScreen().requestRender();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("cards", cards);
        outState.putStringArrayList("playersName", playersName);
        outState.putIntegerArrayList("directions", directions);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onReceiveCard(ArrayList<String> cards, boolean[] upsidedowns) {
        for (int i = 0; i < cards.size(); i++) {
            cardImage.addCard(cards.get(i), upsidedowns[i]);
        }
        getScreen().requestRender();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            setServiceSendCard(mService, true);
            Message msg = Message.obtain();
            msg.what = AbstractWifiDirectGameService.MSG_CLIENT;
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
                onReceiveCard(msg.getData().getStringArrayList("Cards"),
                        msg.getData().getBooleanArray("upsidedown"));
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

    private synchronized void sendMessageToService(Message msg) {
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
        handler.removeCallbacks(null);
        PlayingCardsApplication.getInstance().stopServices();
        unbindService(mConnection);
        super.onDestroy();
    }
}
