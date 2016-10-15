package tcc.ronaldoyoshio.playingcards.touchEventHandler;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.service.GameService;

/**
 * Classe que lida com o envio das cartas.
 * Created by mori on 13/09/16.
 */
public class SendCard implements OnSendCard {
    private MotionCardImage cardImage;
    private final ArrayList<String> playersName;
    private final ArrayList<Integer> directions;

    private Messenger mService;

    private boolean mBound = false;

    public SendCard(MotionCardImage cardImage,
                    ArrayList<String> playersName,
                    ArrayList<Integer> directions,
                    Messenger mService) {
        this.mService = mService;
        this.cardImage = cardImage;
        this.playersName = playersName;
        this.directions = directions;
    }

    @Override
    public void onSendCard(int pointerId, ArrayList<String> cards, int x, int y) {
        SendCardTouchEventHandler sendCardTouchEventHandler =
                cardImage.getSendCardTouchEventHandler();
        String targetPlayerName = sendCardTouchEventHandler.computeNearestPlayerName(
                playersName,
                directions,
                x,
                y
        );

        Message message = Message.obtain();
        message.what = GameService.MSG_SEND_CARD;
        Bundle bundle = new Bundle();
        bundle.putString("Player", targetPlayerName);
        bundle.putStringArrayList("Cards", cards);
        message.setData(bundle);
        sendMessageToService(message);

        System.out.println("Enviando " + cards + " para :" + targetPlayerName);
        cardImage.removeCardsAtPointer(pointerId);
    }

    public void setmBound(boolean mBound) {
        this.mBound = mBound;
    }

    public void setmService(Messenger mService) {
        this.mService = mService;
    }

    private void sendMessageToService(Message msg) {
        if (!mBound || mService == null) return;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
