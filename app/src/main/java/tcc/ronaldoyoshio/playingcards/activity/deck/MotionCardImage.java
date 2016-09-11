package tcc.ronaldoyoshio.playingcards.activity.deck;

import android.content.Context;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;

/**
 * Abstração de carta que se move.
 * Created by mori on 11/09/16.
 */
public class MotionCardImage extends CardImage {
    public List<GLObject> activeCards = Collections.synchronizedList(new ArrayList<GLObject>());
    public List<Integer> activeCardsIndex = Collections.synchronizedList(new ArrayList<Integer>());
    public List<String> activeCardsNames = Collections.synchronizedList(new ArrayList<String>());

    public MotionCardImage(final GLActivity glActivity) {
        addTouchEventHandler(new TouchEventHandler() {
            public float downY;
            public float downX;
            TimerTask timerTask = null;
            long previousDownTime = Long.MIN_VALUE;
            private static final int DELAY = 500;
            @Override
            public boolean onDown(int pointerId, float x, float y) {
                final float glx = getGLX(x, getWidth());
                final float gly = getGLY(y, getHeight());
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

        addTouchEventHandler(new TouchEventHandler(){
            public int[] neighborhoods = new int[3];
            public double[] mins = new double[neighborhoods.length];
            public int[] traceData = new int[5];
            public static final int N = 1;
            public static final int P = 0;
            private final ArrayList<int[]> samples = new ArrayList<>(Arrays.asList(
                    new int[] {0, 0, 0, 0, 0}, // 1
                    new int[] {246, 37348, 52048, 172, -124}, // 2
                    new int[] {134, 18149, 32029, -32, -631}, // 3
                    new int[] {386, 52502, 120184, -107, -611}, // 4
                    new int[] {306, 40104, 56256, -74, -1036}, // 5
                    new int[] {198, 27595, 17286, 19, 6}, // 6
                    new int[] {274, 41636, 92313, 52, 53}, // 7
                    new int[] {270, 43491, 72131, 280, 854}, // 8
                    new int[] {298, 37673, 83028, 208, -1072}, // 9
                    new int[] {330, 49664, 51265, 3, -690}, // 10
                    new int[] {186, 27694, 27554, -47, -740}, // 11
                    new int[] {218, 27981, 60799, -181, -42}, // 12
                    new int[] {170, 24378, 40933, 187, -46}, // 13
                    new int[] {266, 40300, 58356, -112, -265}, // 14
                    new int[] {158, 22765, 39143, 95, 1175}, // 15
                    new int[] {138, 20623, 31656, 259, 24}, // 16
                    new int[] {194, 27440, 44080, -13, -28}, // 17
                    new int[] {350, 49950, 79980, -80, -15}, // 18
                    new int[] {278, 38875, 61429, 295, 1156}, // 19
                    new int[] {114, 14276, 18913, -182, -554} // 20
            ));
            private final int[] types = new int[] {
                    N, // 1
                    P, // 2
                    N, // 3
                    N, // 4
                    N, // 5
                    P, // 6
                    P, // 7
                    P, // 8
                    N, // 9
                    N, // 10
                    N, // 11
                    P, // 12
                    P, // 13
                    P, // 14
                    N, // 15
                    P, // 16
                    P, // 17
                    P, // 18
                    N, // 19
                    N // 20
            };
            private Vector<Integer> trace = new Vector<>();
            private HashMap<Integer, GLObject> pointerCards = new HashMap<>();
            @Override
            public boolean onDown(int pointerId, float x, float y) {
                if (!activeCards.isEmpty()) {
                    return true;
                }
                int index = findFirstCardIndexAt(getGLX(x, getWidth()), getGLY(y, getHeight()));
                if (index >= 0) {
                    putPointerCards(pointerId, index);
                }

                if (index >= 0) {
                    overAll(index);
                }

                trace.clear();
                trace.add((int) x);
                trace.add((int) y);

                return index >= 0;
            }

            @Override
            public boolean onMove(int pointerId, float x, float y, float dx, float dy) {
                if (!activeCards.isEmpty() && pointerId == 0
                        && findFirstCardIndexAt(getGLX(x, getWidth()), getGLY(y, getHeight()))>=0) {
                    trace.add((int) x);
                    trace.add((int) y);
                    trace.add((int) dx);
                    trace.add((int) dy);
                    for (GLObject card :
                            activeCards) {
                        setProjectionCoords(dx, dy, getWidth(), getHeight());
                        positionUpdate(card.getFloats("position"));
                    }
                    return true;
                }

                if (pointerCards.isEmpty()) {
                    return false;
                }

                // Criando a matriz de projeção do modelo para a tela, idêntico ao do shader.
                setProjectionCoords(dx, dy, getWidth(), getHeight());
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
                if (activeCards.size() == totalCards) {
                    int index, sampleIndex = 0;
                    for (index = 0; index < mins.length; index++) {
                        mins[index] = Double.POSITIVE_INFINITY;
                        neighborhoods[index] = N;
                    }

                    traceData[0] = trace.size();
                    traceData[1] = trace.get(0);
                    traceData[2] = trace.get(1);
                    traceData[3] = 0;
                    traceData[4] = 0;
                    for (index = 2; index < trace.size(); index += 4) {
                        traceData[1] += trace.get(index);
                        traceData[2] += trace.get(index + 1);
                        traceData[3] += trace.get(index + 2);
                        traceData[4] += trace.get(index + 3);
                    }

                    for (int[] sample :
                            samples) {
                        double distance = 0;
                        System.out.println("======== " + sampleIndex + " ========");
                        distance += Math.abs(sample[0] - traceData[0]);
                        System.out.println(Math.abs(sample[0] - traceData[0]));
                        distance += Math.abs(sample[1] - traceData[1]);
                        System.out.println(Math.abs(sample[1] - traceData[1]));
                        distance += Math.abs(sample[2] - traceData[2]);
                        System.out.println(Math.abs(sample[2] - traceData[2]));
                        distance += Math.abs(sample[3] - traceData[3]);
                        System.out.println(Math.abs(sample[3] - traceData[3]));
                        distance += Math.abs(sample[4] - traceData[4]);
                        System.out.println(Math.abs(sample[4] - traceData[4]));
                        for (index = 0; index < mins.length; index++) {
                            if (distance < mins[index]) {
                                mins[index] = distance;
                                neighborhoods[index] = types[sampleIndex];
                                break;
                            }
                        }
                        sampleIndex++;
                    }
                    if (isShuffle()) {
                        System.out.println("É shuffle");
                        Collections.shuffle(activeCardsIndex);
                        for (index = 0; index < activeCardsIndex.size(); index++) {
                            getObjects().set(activeCardsIndex.get(index), activeCards.get(index));
                            cards.set(activeCardsIndex.get(index), activeCardsNames.get(index));
                        }
                        activeCards.clear();
                        activeCardsNames.clear();
                        for (index = 0; index < activeCardsIndex.size(); index++) {
                            activeCards.add(getObjects().get(index));
                            activeCardsNames.add(cards.get(index));
                        }
                        requestRender();
                        Vibrator v = (Vibrator) glActivity.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                    } else {
                        System.out.println("Não é shuffle");
                    }
                    System.out.println(Arrays.toString(traceData));
                }
                pointerCards.clear();
                return false;
            }

            private boolean isShuffle() {
                int count = 0;
                for (int neighborhood :
                        neighborhoods) {
                    if (neighborhood == P) {
                        count++;
                    }
                }
                System.out.println(count);
                return count > neighborhoods.length / 2;
            }

            @Override
            public boolean onPointerDown(int pointerId, float x, float y) {
                if (!activeCards.isEmpty()) {
                    return false;
                }
                int index = findFirstCardIndexAt(getGLX(x, getWidth()), getGLY(y, getHeight()));
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
