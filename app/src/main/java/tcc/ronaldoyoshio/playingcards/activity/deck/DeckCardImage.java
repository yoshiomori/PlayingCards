package tcc.ronaldoyoshio.playingcards.activity.deck;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.touchEventHandler.TouchEventHandler;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.OnSendCard;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.SendCardTouchEventHandler;

/**
 * Desenhando uma carta de baralho
 * Created by mori on 15/07/16.
 */
public class DeckCardImage extends MotionCardImage {

    public DeckCardImage(
            final DeckActivity deckActivity,
            final ArrayList<String> playersName,
            final ArrayList<Integer> directions
    ) {
        super(deckActivity);

        setOnSendCard(new OnSendCard() {
            @Override
            public void onSendCard(int pointerId, int x, int y) {
                SendCardTouchEventHandler sendCardTouchEventHandler = getSendCardTouchEventHandler();
                String targetPlayerName = sendCardTouchEventHandler.computeNearestPlayerName(
                        playersName,
                        directions,
                        x,
                        y
                );

                System.out.println("Enviando para :" + targetPlayerName);

                for (GLObject card : getActiveCards()) {
                    if (card == getPointerCards().get(pointerId)) {
                        System.out.println("Active Cards contém pointer Cards");
                    }
                }
                if (getActiveCards().isEmpty()) {
                    getCards().remove(getObjects().indexOf(getPointerCards().get(pointerId)));
                    getObjects().remove(getPointerCards().get(pointerId));
                }
                else {
                    for (GLObject card : getActiveCards()) {
                        getCards().remove(getObjects().indexOf(card));
                    }
                    getObjects().removeAll(getActiveCards());
                }
                getMotionTouchEventHandler().deactivateCards();
            }
        });

        /* Quando der duplo taps a carta vira */
        addTouchEventHandler(new TouchEventHandler() {
            public long previousDownTime = Long.MIN_VALUE;
            public float previousX = Float.POSITIVE_INFINITY;
            public float previousY = Float.POSITIVE_INFINITY;
            public boolean doubleTap;
            public GLObject previousCard;
            @Override
            public boolean onDown(int pointerId, float x, float y) {
                // Verificando se é double tap
                long downTime = System.currentTimeMillis();
                int index = findFirstCardIndexAt(x, getWidth(), y, getHeight(), getObjects());
                if (index >= 0) {
                    GLObject currentCard = getObjects().get(index);
                    doubleTap = isDoubleTap(downTime - previousDownTime, x - previousX, y - previousY, currentCard);
                    previousDownTime = downTime;
                    previousX = x;
                    previousY = y;
                    previousCard = currentCard;

                    if (doubleTap) {
                        doubleTap = false;
                        if (getActiveCards().isEmpty()) {
                            flipCard(getObjects().get(index), index);
                        } else {
                            if (getActiveCards().contains(getObjects().get(index))) {
                                for (GLObject card :
                                        getActiveCards()) {
                                    flipCard(card, getObjects().indexOf(card));
                                }

                            }
                        }
                    }
                }

                return false;
            }

            public boolean isDoubleTap(long dt, float dx, float dy, GLObject card) {
                return dx * dx + dy * dy <= 1000 && dt * dt <= 100000 && previousCard == card;
            }
        });
    }

    private void flipCard(GLObject card, int index) {
        if (cardData.getCardCoord("Back") == card.getFloats("card_coord")) {
            card.set("card_coord", cardData.getCardCoord(cards.get(index)));
        }
        else {
            card.set("card_coord", cardData.getCardCoord("Back"));
        }
    }
}
