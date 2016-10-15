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

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.images.CardImage;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.model.Cards;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;
import tcc.ronaldoyoshio.playingcards.service.GameService;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.OnSendCard;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.SendCard;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.TouchEventHandler;

public class DeckActivity extends GLActivity implements Handler.Callback {
    public static final int MSG_RECEIVE_CARD = 1 ;
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

        /* Quando der duplo taps a carta vira */
        cardImage.addTouchEventHandler(new TouchEventHandler() {
            long previousDownTime = Long.MIN_VALUE;
            float previousX = Float.POSITIVE_INFINITY;
            float previousY = Float.POSITIVE_INFINITY;
            boolean doubleTap;
            GLObject previousCard;
            @Override
            public boolean onDown(int pointerId, float x, float y) {
                // Verificando se é double tap
                long downTime = System.currentTimeMillis();
                int index = findFirstCardIndexAt(
                        x, getWidth(), y, getHeight(), cardImage.getObjects());
                if (index >= 0) {
                    GLObject currentCard = cardImage.getObjects().get(index);
                    doubleTap = isDoubleTap(
                            downTime - previousDownTime, x - previousX, y - previousY, currentCard);
                    previousDownTime = downTime;
                    previousX = x;
                    previousY = y;
                    previousCard = currentCard;

                    if (doubleTap) {
                        doubleTap = false;
                        if (cardImage.getActiveCards().isEmpty()) {
                            flipCard(cardImage.getObjects().get(index), index);
                        } else {
                            if (cardImage.getActiveCards().contains(
                                    cardImage.getObjects().get(index))) {
                                for (GLObject card :
                                        cardImage.getActiveCards()) {
                                    flipCard(card, cardImage.getObjects().indexOf(card));
                                }

                            }
                        }
                    }
                }

                return false;
            }

            boolean isDoubleTap(long dt, float dx, float dy, GLObject card) {
                return dx * dx + dy * dy <= 1000 && dt * dt <= 100000 && previousCard == card;
            }

            private void flipCard(GLObject card, int index) {
                CardImage.CardData cardData = cardImage.getCardData();
                if (cardData.getCardCoord("Back") == card.getFloats("card_coord")) {
                    card.set("card_coord", cardData.getCardCoord(cards.get(index)));
                }
                else {
                    card.set("card_coord", cardData.getCardCoord("Back"));
                }
            }
        });

        sendCardEvent = new SendCard(cardImage, playersName, directions, mService);
        cardImage.setOnSendCard(sendCardEvent);
        addImage(cardImage);

        super.onCreate(savedInstanceState);

        print(cards);
        bindService(new Intent(this, GameServerService.class), mConnection, 0);
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

    private void onReceiveCard(ArrayList<String> cards) {
        Log.d("Deck", String.valueOf(cards.size()));
        for (String card :
                cards) {
            cardImage.addCard(card);
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
}
