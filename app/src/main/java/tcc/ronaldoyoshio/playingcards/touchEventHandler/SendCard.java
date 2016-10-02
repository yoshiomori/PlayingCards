package tcc.ronaldoyoshio.playingcards.touchEventHandler;

import android.os.Messenger;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;

/**
 * Classe que lida com o envio das cartas.
 * Created by mori on 13/09/16.
 */
public class SendCard implements OnSendCard {
    private MotionCardImage cardImage;
    private final ArrayList<String> playersName;
    private final ArrayList<Integer> directions;
    private Messenger mService;

    public SendCard(MotionCardImage cardImage,
                    ArrayList<String> playersName,
                    ArrayList<Integer> directions,
                    Messenger mService) {
        this.cardImage = cardImage;
        this.playersName = playersName;
        this.directions = directions;
        this.mService = mService;
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

        System.out.println("Enviando " + cards + " para :" + targetPlayerName);
        cardImage.removeCardsAtPointer(pointerId);
    }
}
