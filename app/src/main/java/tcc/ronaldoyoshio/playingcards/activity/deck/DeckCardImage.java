package tcc.ronaldoyoshio.playingcards.activity.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tcc.ronaldoyoshio.playingcards.GL.GLObject;
import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;

/**
 * Desenhando uma carta de baralho
 * Created by mori on 15/07/16.
 */
public class DeckCardImage extends CardImage {
    public ArrayList<GLObject> activeCards = new ArrayList<>();

    public DeckCardImage() {
        addTouchEventHandler(new TouchEventHandler() {
            public float downY;
            public float downX;
            TimerTask timerTask = null;
            long previousDownTime = Long.MIN_VALUE;
            private static final int DELAY = 500;
            @Override
            public boolean onDown(int pointerId, float x, float y, int width, int height) {
                final float glx = getGLX(x, width);
                final float gly = getGLY(y, height);
                final int cardIndex = findFirstCardIndexAt(glx, gly);
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
                            activateCards(glx, gly);
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(timerTask, DELAY);
                    return false;
                }
                else {
                    activateCards(glx, gly);
                    return true;
                }
            }

            private void deactivateCards() {
                for (GLObject card :
                        getObjects()) {
                    card.set("blue_tone", 0);
                }
                activeCards.clear();
            }

            @Override
            public boolean onMove(int pointerId, float x, float y, float dx, float dy, int width, int height) {
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

            private void activateCards(float x, float y) {
                int index;
                List<GLObject> objects = getObjects();
                float[] lastCardPosition = objects.get(objects.size() - 1).getFloats("position");

                // Procurando a última das cartas que contém o ponto x, y
                for (index = objects.size() - 1; index >= 0; index--) {
                    GLObject card = objects.get(index);
                    setModelCoord(x, y, card);

                    // Se o ponto x, y está contido no modelo da carta então o ponto foi encontrado
                    // e o laço é quebrado.
                    if (cardHit()) {
                        activeCards.add(card);
                        float[] position = card.getFloats("position");
                        System.arraycopy(lastCardPosition, 0, position, 0, position.length);
                        card.set("blue_tone", 0.2f);
                    }
                }
                requestRender();
            }
        });

        addTouchEventHandler(new TouchEventHandler() {
            public long previousDownTime = Long.MIN_VALUE;
            public float previousX = Float.POSITIVE_INFINITY;
            public float previousY = Float.POSITIVE_INFINITY;
            public boolean doubleTap;
            public GLObject previousCard;
            @Override
            public boolean onDown(int pointerId, float x, float y, int width, int height) {
                // Verificando se é double tap
                long downTime = System.currentTimeMillis();
                int index = findFirstCardIndexAt(getGLX(x, width), getGLY(y, height));
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

        addTouchEventHandler(new TouchEventHandler(){
            private HashMap<Integer, GLObject> pointerCards = new HashMap<>();
            @Override
            public boolean onDown(int pointerId, float x, float y, int width, int height) {
                if (!activeCards.isEmpty()) {
                    return true;
                }
                int index = findFirstCardIndexAt(getGLX(x, width), getGLY(y, height));
                if (index >= 0) {
                    putPointerCards(pointerId, index);
                }

                if (index >= 0) {
                    overAll(index);
                }
                return index >= 0;
            }

            @Override
            public boolean onMove(int pointerId, float x, float y, float dx, float dy, int width, int height) {
                if (!activeCards.isEmpty() && pointerId == 0
                        && findFirstCardIndexAt(getGLX(x, width), getGLY(y, height))>=0) {
                    for (GLObject card :
                            activeCards) {
                        setProjectionCoords(dx, dy, width, height);
                        positionUpdate(card.getFloats("position"));
                    }
                    return true;
                }

                if (pointerCards.isEmpty()) {
                    return false;
                }

                // Criando a matriz de projeção do modelo para a tela, idêntico ao do shader.
                setProjectionCoords(dx, dy, width, height);
                if (pointerCards.containsKey(pointerId)) {
                    GLObject card = pointerCards.get(pointerId);
                    positionUpdate(card.getFloats("position"));
                }
                return true;
            }


            @Override
            public boolean onUp() {
                if (pointerCards.isEmpty()) {
                    return false;
                }
                pointerCards.clear();
                return false;
            }

            @Override
            public boolean onPointerDown(int pointerId, float x, float y, int width, int height) {
                if (!activeCards.isEmpty()) {
                    return false;
                }
                int index = findFirstCardIndexAt(getGLX(x, width), getGLY(y, height));
                if (index >= 0) {
                    putPointerCards(pointerId, index);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onPointerUp(int pointerId) {
                pointerCards.remove(pointerId);
                return true;
            }

            /**
             * Adiciona a carta de índice index
             * @param pointerId Identificador do ponteiro que será inserido a carta
             * @param index Indice da List de GLObjects, ou índice da carta.
             */
            private void putPointerCards(int pointerId, int index) {
                GLObject card = getObjects().get(index);
                if (!pointerCards.values().contains(card)) {
                    pointerCards.put(pointerId, card);
                }
            }
        });

        addTouchEventHandler(new ShuffleEventHandler());
    }

    private void flipCard(GLObject card, int index) {
        if (cardData.getCardCoord("Back") == card.getFloats("card_coord")) {
            card.set("card_coord", cardData.getCardCoord(cards.get(index)));
        }
        else {
            card.set("card_coord", cardData.getCardCoord("Back"));
        }
    }

    private void positionUpdate(float[] position) {
        position[0] += getV()[0];
        position[1] += getV()[1];
    }

    private void overAll(int index) {
        List<GLObject> objects = getObjects();
        for (int i = index; i < objects.size()-1; i++) {
            Collections.swap(objects, i, i+1);
            Collections.swap(cards, i, i+1);
        }
    }
}
