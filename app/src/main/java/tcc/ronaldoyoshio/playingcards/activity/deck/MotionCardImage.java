package tcc.ronaldoyoshio.playingcards.activity.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;

/**
 * Abstração de carta que se move.
 * Created by mori on 11/09/16.
 */
public class MotionCardImage extends CardImage {
    private final MotionTouchEventHandler motionTouchEventHandler;
    private List<GLObject> activeCards = Collections.synchronizedList(new ArrayList<GLObject>());
    private List<Integer> activeCardsIndex = Collections.synchronizedList(new ArrayList<Integer>());
    private List<String> activeCardsNames = Collections.synchronizedList(new ArrayList<String>());
    private HashMap<Integer, GLObject> pointerCards = new HashMap<>();
    private OnSendCard onSendCard;

    public MotionCardImage(final GLActivity glActivity) {

        /* Tratamento de toque na borda */
        SendCardTouchEventHandler sendCardTouchEventHandler = new SendCardTouchEventHandler(this, glActivity);
        addTouchEventHandler(sendCardTouchEventHandler);

        /* Tratamento de toque na carta */
        addTouchEventHandler(new TouchEventHandler() {
            public float downY;
            public float downX;
            TimerTask timerTask = null;
            long previousDownTime = Long.MIN_VALUE;
            private static final int DELAY = 500;
            @Override
            public boolean onDown(int pointerId, float x, float y) {
                final int cardIndex = findFirstCardIndexAt(x, getWidth(), y, getHeight(), getObjects());
                if (cardIndex < 0) {
                    deactivateCards();
                    return true;
                }

                GLObject card = getObjects().get(cardIndex);
                if (activeCards.contains(card)) {
                    return false;
                }

                if (activeCards.isEmpty()) {
                    long downTime = System.currentTimeMillis();
                    if (previousDownTime == Long.MIN_VALUE) {
                        previousDownTime = downTime;
                    }
                    if (previousDownTime - downTime < DELAY && timerTask != null) {
                        timerTask.cancel();
                    }
                    previousDownTime = downTime;
                    downX = x;
                    downY = y;
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            activateCards(downX, getWidth(), downY, getHeight());
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(timerTask, DELAY);
                    return false;
                }
                else {
                    activateCards(x, getWidth(), y, getHeight());
                    return true;
                }
            }

            private void deactivateCards() {
                for (GLObject card :
                        getObjects()) {
                    card.set("blue_tone", 0);
                }
                activeCards.clear();
                activeCardsIndex.clear();
                activeCardsNames.clear();
            }

            @Override
            public boolean onMove(int pointerId, float x, float y, float dx, float dy) {
                if ((x - downX)*(x - downX)+(y - downY)*(y - downY) > 100) {
                    if (timerTask != null) {
                        timerTask.cancel();
                    }
                }
                return false;
            }

            @Override
            public boolean onUp() {
                if (System.currentTimeMillis() - previousDownTime < DELAY) {
                    if (timerTask != null) {
                        timerTask.cancel();
                    }
                }
                return false;
            }

            private void activateCards(float x, int width, float y, int height) {
                int index;
                List<GLObject> objects = getObjects();
                float[] lastCardPosition = objects.get(objects.size() - 1).getFloats("position");

                // Procurando a última das cartas que contém o ponto x, y
                for (index = objects.size() - 1; index >= 0; index--) {
                    GLObject card = objects.get(index);
                    setModelCoord(x, width, y, height, card);

                    // Se o ponto x, y está contido no modelo da carta então o ponto foi encontrado
                    // e o laço é quebrado.
                    if (cardHit()) {
                        activeCards.add(card);
                        activeCardsIndex.add(index);
                        activeCardsNames.add(cards.get(index));
                        float[] position = card.getFloats("position");
                        System.arraycopy(lastCardPosition, 0, position, 0, position.length);
                        card.set("blue_tone", 0.2f);
                    }
                }
                requestRender();
            }
        });

        /* Tratamento de movimento */
        motionTouchEventHandler = new MotionTouchEventHandler(this, glActivity);
        addTouchEventHandler(motionTouchEventHandler);
    }

    public void setOnSendCard(OnSendCard onSendCard) {
        this.onSendCard = onSendCard;
    }

    public List<GLObject> getActiveCards() {
        return activeCards;
    }

    public HashMap<Integer, GLObject> getPointerCards() {
        return pointerCards;
    }

    public List<Integer> getActiveCardsIndex() {
        return activeCardsIndex;
    }

    public List<String> getActiveCardsNames() {
        return activeCardsNames;
    }

    public OnSendCard getOnSendCard() {
        return onSendCard;
    }

    public MotionTouchEventHandler getMotionTouchEventHandler() {
        return motionTouchEventHandler;
    }
}
