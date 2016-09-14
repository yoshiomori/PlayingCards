package tcc.ronaldoyoshio.playingcards.activity.deck;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.OnSendCard;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.SendCardTouchEventHandler;

/**
 * Classe que lida com o envio das cartas.
 * Created by mori on 13/09/16.
 */
public class SendCard implements OnSendCard {
    private MotionCardImage cardImage;
    private final ArrayList<String> playersName;
    private final ArrayList<Integer> directions;

    public SendCard(MotionCardImage cardImage, ArrayList<String> playersName, ArrayList<Integer> directions) {
        this.cardImage = cardImage;
        this.playersName = playersName;
        this.directions = directions;
    }

    @Override
    public void onSendCard(int pointerId, int x, int y) {
        SendCardTouchEventHandler sendCardTouchEventHandler =
                cardImage.getSendCardTouchEventHandler();
        String targetPlayerName = sendCardTouchEventHandler.computeNearestPlayerName(
                playersName,
                directions,
                x,
                y
        );

        System.out.println("Enviando para :" + targetPlayerName);
        cardImage.removeCardsAtPointer(pointerId);
    }
}
