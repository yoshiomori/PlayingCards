package tcc.ronaldoyoshio.playingcards.activity.deck;

import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;

/**
 * Desenhando uma carta de baralho
 * Created by mori on 15/07/16.
 */
public class DeckCardImage extends MotionCardImage {

    public DeckCardImage(final DeckActivity deckActivity) {
        super(deckActivity);
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
                int index = findFirstCardIndexAt(getGLX(x, getWidth()), getGLY(y, getHeight()));
                if (index >= 0) {
                    GLObject currentCard = getObjects().get(index);
                    doubleTap = isDoubleTap(downTime - previousDownTime, x - previousX, y - previousY, currentCard);
                    previousDownTime = downTime;
                    previousX = x;
                    previousY = y;
                    previousCard = currentCard;

                    if (doubleTap) {
                        doubleTap = false;
                        if (activeCards.isEmpty()) {
                            flipCard(getObjects().get(index), index);
                        } else {
                            if (activeCards.contains(getObjects().get(index))) {
                                for (GLObject card :
                                        activeCards) {
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
