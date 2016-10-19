package tcc.ronaldoyoshio.playingcards.touchEventHandler;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;

import static android.content.ContentValues.TAG;

/**
 * Trata dos eventos de toque de movimento
 * Created by mori on 12/09/16.
 */
public class MotionTouchEventHandler extends TouchEventHandler {
    private int[] neighborhoods = new int[3];
    private double[] mins = new double[neighborhoods.length];
    private int[] traceData = new int[5];
    private static final int N = 1;
    private static final int P = 0;
    private final ArrayList<int[]> samples = new ArrayList<>(Arrays.asList(
            new int[]{182, 12831, 44521, -346, 32}, // 1
            new int[]{274, 36192, 52292, 35, -53}, // 2
            new int[]{158, 19221, 56594, -1, 796}, // 3
            new int[]{334, 43808, 54444, 27, -727}, // 4
            new int[]{358, 45673, 118836, 9, -701}, // 5
            new int[]{354, 43974, 110122, 3, 694} // 6
    ));
    private final int[] types = new int[]{
            N, // 1
            N, // 2
            N, // 3
            P, // 4
            P, // 4
            P // 5
    };
    private Vector<Integer> trace = new Vector<>();
    private MotionCardImage motionCardImage;
    private GLActivity glActivity;

    public MotionTouchEventHandler(MotionCardImage motionCardImage, GLActivity glActivity) {
        this.motionCardImage = motionCardImage;
        this.glActivity = glActivity;
    }

    @Override
    public boolean onDown(int pointerId, float x, float y) {

        int index = findFirstCardIndexAt(x, getWidth(), y, getHeight(), motionCardImage.getObjects());
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
        if (motionCardImage.getPointerCards().isEmpty()
                && motionCardImage.getActiveCards().isEmpty()) {
            return false;
        }

        if (motionCardImage.getPointerCards().containsKey(pointerId)
                && motionCardImage.getActiveCards().isEmpty()) {
            movePointerCard(pointerId, dx, dy);
            return true;
        }

        synchronized (motionCardImage.getActiveCards()) {
        List<GLObject> activeCards = motionCardImage.getActiveCards();
        // Movendo todas as cartas ativas
        if (!activeCards.isEmpty() && pointerId == 0
                && findFirstCardIndexAt(x, getWidth(), y, getHeight(), motionCardImage.getObjects()) >= 0) {

            // Registrando o movimento do dedo
            trace.add((int) x);
            trace.add((int) y);
            trace.add((int) dx);
            trace.add((int) dy);

            // Cada uma das cartas ativas está sendo atualizado
                for (GLObject card :
                        activeCards) {
                    setProjectionCoords(dx, dy, getWidth(), getHeight());
                    positionUpdate(card.getFloats("position"));
                }
            }
        }

        return true;
    }

    /**
     * Atualiza a carta indicada pelo dedo pointerId
     * O canto superior esquerdo da tela é a coordenada (0, 0)
     * O canto inferior direito da tela é a coordenada (width, height);
     *
     * @param pointerId Cada dedo na tela tem uma identificação pointerId
     * @param dx        deslocamento no eixo x da tela
     * @param dy        deslocamento no eixo y da tela
     */
    private void movePointerCard(int pointerId, float dx, float dy) {
        // Criando a matriz de projeção do modelo para a tela, idêntico ao do shader.
        setProjectionCoords(dx, dy, getWidth(), getHeight());
        if (motionCardImage.getPointerCards().containsKey(pointerId)) {
            GLObject card = motionCardImage.getPointerCards().get(pointerId);
            positionUpdate(card.getFloats("position"));
        }
    }


    @Override
    public boolean onUp() {
        List<GLObject> activeCards = motionCardImage.getActiveCards();
        if (motionCardImage.getPointerCards().isEmpty()) {
            return false;
        }
        if (activeCards.size() == motionCardImage.getTotalCards()) {
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

            Log.d(TAG, "onUp: " + Arrays.toString(traceData));

            for (int[] sample :
                    samples) {
                double distance = 0;
                distance += Math.abs(sample[0] - traceData[0]);
                distance += Math.abs(sample[1] - traceData[1]);
                distance += Math.abs(sample[2] - traceData[2]);
                distance += Math.abs(sample[3] - traceData[3]);
                distance += Math.abs(sample[4] - traceData[4]);
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
                Collections.shuffle(motionCardImage.getActiveCardsIndex());
                for (index = 0; index < motionCardImage.getActiveCardsIndex().size(); index++) {
                    motionCardImage.getObjects().set(motionCardImage.getActiveCardsIndex().get(index), activeCards.get(index));
                    motionCardImage.getCards().set(motionCardImage.getActiveCardsIndex().get(index), motionCardImage.getActiveCardsNames().get(index));
                }
                activeCards.clear();
                motionCardImage.getActiveCardsNames().clear();
                for (index = 0; index < motionCardImage.getActiveCardsIndex().size(); index++) {
                    activeCards.add(motionCardImage.getObjects().get(index));
                    motionCardImage.getActiveCardsNames().add(motionCardImage.getCards().get(index));
                }
                requestRender();
                Vibrator v = (Vibrator) glActivity.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        }
        motionCardImage.getPointerCards().clear();
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
        return count > neighborhoods.length / 2;
    }

    @Override
    public boolean onPointerDown(int pointerId, float x, float y) {
        List<GLObject> activeCards = motionCardImage.getActiveCards();
        if (!activeCards.isEmpty()) {
            return false;
        }
        int index = findFirstCardIndexAt(x, getWidth(), y, getHeight(), motionCardImage.getObjects());
        if (index >= 0) {
            putPointerCards(pointerId, index);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPointerUp(int pointerId) {
        motionCardImage.getPointerCards().remove(pointerId);
        return true;
    }

    /**
     * Adiciona a carta de índice index
     *
     * @param pointerId Identificador do ponteiro que será inserido a carta
     * @param index     Indice da List de GLObjects, ou índice da carta.
     */
    private void putPointerCards(int pointerId, int index) {
        GLObject card = motionCardImage.getObjects().get(index);
        if (!motionCardImage.getPointerCards().values().contains(card)) {
            motionCardImage.getPointerCards().put(pointerId, card);
        }
    }

    private void positionUpdate(float[] position) {
        position[0] += getV()[0];
        position[1] += getV()[1];
    }

    private void overAll(int index) {
        List<GLObject> objects = motionCardImage.getObjects();
        for (int i = index; i < objects.size()-1; i++) {
            Collections.swap(objects, i, i+1);
            Collections.swap(motionCardImage.getCards(), i, i+1);
        }
    }

    public void deactivateCards() {
        for (GLObject card :
                motionCardImage.getObjects()) {
            card.set("blue_tone", 0);
        }
        motionCardImage.getActiveCards().clear();
        motionCardImage.getActiveCardsIndex().clear();
        motionCardImage.getActiveCardsNames().clear();
        motionCardImage.getPointerCards().clear();
    }
}
