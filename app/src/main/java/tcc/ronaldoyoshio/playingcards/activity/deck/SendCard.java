package tcc.ronaldoyoshio.playingcards.activity.deck;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.OnSendCard;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.SendCardTouchEventHandler;

/**
 * Classe que lida com o envio das cartas.
 * Created by mori on 13/09/16.
 */
public class SendCard implements OnSendCard {
    private DeckCardImage cardImage;
    private final ArrayList<String> playersName;
    private final ArrayList<Integer> directions;

    public SendCard(DeckCardImage cardImage, ArrayList<String> playersName, ArrayList<Integer> directions) {
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

        for (GLObject card : cardImage.getActiveCards()) {
            if (card == cardImage.getPointerCards().get(pointerId)) {
                System.out.println("Active Cards contém pointer Cards");
            }
        }
        if (cardImage.getActiveCards().isEmpty()) {
            cardImage.getCards().remove(cardImage.getObjects().indexOf(
                    cardImage.getPointerCards().get(pointerId))
            );
            cardImage.getObjects().remove(cardImage.getPointerCards().get(pointerId));
        }
        else {
            for (GLObject card : cardImage.getActiveCards()) {
                cardImage.getCards().remove(cardImage.getObjects().indexOf(card));
            }
            cardImage.getObjects().removeAll(cardImage.getActiveCards());
        }
        cardImage.getMotionTouchEventHandler().deactivateCards();
    }
}
