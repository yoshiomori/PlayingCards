package tcc.ronaldoyoshio.playingcards.touchEventHandler;

import android.content.Context;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;

/**
 * Trata dos eventos de toque de movimento
 * Created by mori on 12/09/16.
 */
public class MotionTouchEventHandler extends TouchEventHandler {
    public int[] neighborhoods = new int[3];
    public double[] mins = new double[neighborhoods.length];
    public int[] traceData = new int[5];
    public static final int N = 1;
    public static final int P = 0;
    private final ArrayList<int[]> samples = new ArrayList<>(Arrays.asList(
            new int[]{0, 0, 0, 0, 0}, // 1
            new int[]{246, 37348, 52048, 172, -124}, // 2
            new int[]{134, 18149, 32029, -32, -631}, // 3
            new int[]{386, 52502, 120184, -107, -611}, // 4
            new int[]{306, 40104, 56256, -74, -1036}, // 5
            new int[]{198, 27595, 17286, 19, 6}, // 6
            new int[]{274, 41636, 92313, 52, 53}, // 7
            new int[]{270, 43491, 72131, 280, 854}, // 8
            new int[]{298, 37673, 83028, 208, -1072}, // 9
            new int[]{330, 49664, 51265, 3, -690}, // 10
            new int[]{186, 27694, 27554, -47, -740}, // 11
            new int[]{218, 27981, 60799, -181, -42}, // 12
            new int[]{170, 24378, 40933, 187, -46}, // 13
            new int[]{266, 40300, 58356, -112, -265}, // 14
            new int[]{158, 22765, 39143, 95, 1175}, // 15
            new int[]{138, 20623, 31656, 259, 24}, // 16
            new int[]{194, 27440, 44080, -13, -28}, // 17
            new int[]{350, 49950, 79980, -80, -15}, // 18
            new int[]{278, 38875, 61429, 295, 1156}, // 19
            new int[]{114, 14276, 18913, -182, -554} // 20
    ));
    private final int[] types = new int[]{
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
    private MotionCardImage motionCardImage;
    private GLActivity glActivity;

    public MotionTouchEventHandler(MotionCardImage motionCardImage, GLActivity glActivity) {
        this.motionCardImage = motionCardImage;
        this.glActivity = glActivity;
    }

    @Override
    public boolean onDown(int pointerId, float x, float y) {
        List<GLObject> activeCards = motionCardImage.getActiveCards();
        if (!activeCards.isEmpty()) {
            return true;
        }
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
            return true;
        }

        if (motionCardImage.getPointerCards().isEmpty()) {
            return false;
        }
        movePointerCard(pointerId, dx, dy);

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
    public void movePointerCard(int pointerId, float dx, float dy) {
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
