package tcc.ronaldoyoshio.playingcards.touchEventHandler;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.service.wifidirect.AbstractWifiDirectGameService;

/**
 * Classe que lida com o envio das cartas.
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
    public void onSendCard(int pointerId, ArrayList<String> cards, int x, int y, ArrayList<Boolean> upsidedown) {
        SendCardTouchEventHandler sendCardTouchEventHandler =
                cardImage.getSendCardTouchEventHandler();
        String targetPlayerName = sendCardTouchEventHandler.computeNearestPlayerName(
                playersName,
                directions,
                x,
                y
        );

        Message message = Message.obtain();
        message.what = AbstractWifiDirectGameService.MSG_SEND_CARD;
        Bundle bundle = new Bundle();
        bundle.putString("Player", targetPlayerName);
        bundle.putStringArrayList("Cards", cards);
        boolean[] array = new boolean[upsidedown.size()];
        for (int i = 0; i < upsidedown.size(); i++) {
            array[i] = upsidedown.get(i);
        }
        bundle.putBooleanArray("upsidedown", array);
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
