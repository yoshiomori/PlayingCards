package tcc.ronaldoyoshio.playingcards.activity.main;

import android.opengl.Matrix;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tcc.ronaldoyoshio.playingcards.GL.GL;
import tcc.ronaldoyoshio.playingcards.GL.GLImage;
import tcc.ronaldoyoshio.playingcards.GL.GLObject;
import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

/**
 * Desenhando uma carta de baralho
 * Created by mori on 15/07/16.
 */
public class CardImage extends GLImage {
    public static final int SIDEBYSIDE = 1;
    public static final int CENTERED = 0;
    private int mode;
    private CardData cardData = new CardData();
    float[] m = new float[16];
    float[] v = new float[4];
    private PlayingCards cards;
    private float r_width;
    private float r_height;
    public ArrayList<GLObject> activeCards = new ArrayList<>();
    private TouchEventHandler holdEventHandler = new TouchEventHandler() {
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
    };

    private int findFirstCardIndexAt(float glx, float gly) {
        int index;
        final List<GLObject> cards = getObjects();
        for (index = cards.size() - 1; index >= 0; index--){
            setModelCoord(glx, gly, cards.get(index));
            if (cardHit()) {
                break;
            }
        }
        return index;
    }

    private TouchEventHandler flipCardsEventHandler = new TouchEventHandler() {
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
    };

    private TouchEventHandler moveCardsEventHandler = new TouchEventHandler(){
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

            if (pointerId != 0) {
                System.out.println(pointerId);
                System.out.println(index);
            }
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
            pointerCards.put(pointerId, card);
        }
    };

    private void setProjectionCoords(float dx, float dy, int width, int height) {
        // Criando a matriz de projeção do modelo para a tela, idêntico ao do shader.
        setProjectionMatrix();
        MultiplyInvMRhsVec(m, new float[]{getGLDx(dx, width), getGLDy(dy, height), 0, 0});
    }

    @Override
    protected void onSurfaceCreated() {
        setArray(cardData.getArray());
        setElementArray(cardData.getElementArray());
        setShader(
                "/* Vertex Shader */" +
                        "attribute vec2 vertex;" +
                        "uniform float ratio;" +
                        "uniform vec2 position;" +
                        "varying vec2 texture_coord;" +
                        "mat4 projection() {" +
                        "   float width = ratio > 1.0 ? 1.0 / ratio : 1.0;" +
                        "   float height = ratio <= 1.0 ? ratio : 1.0;" +
                        "   return mat4(" +
                        "       width,  0     , 0, 0," +
                        "       0    ,  height, 0, 0," +
                        "       0    ,  0     , 1, 0," +
                        "       0    ,  0     , 0, 1 " +
                        "   );" +
                        "}" +
                        "mat4 model() {" +
                        "   mat4 m;" +
                        "   float s, c, a;" +
                        "   m = mat4(" +
                        "           0.4, 0      , 0, 0," +
                        "           0      , 0.4, 0, 0," +
                        "           0      , 0   , 1, 0," +
                        "           0      , 0   , 0, 1 " +
                        "   );" +
                        "   m *= mat4(" +
                        "            0, 1, 0, 0," +
                        "           -1, 0, 0, 0," +
                        "            0, 0, 1, 0," +
                        "            0, 0, 0, 1 " +
                        "   );" +
                        "   m *= mat4(" +
                        "           1, 0, 0, position.x," +
                        "           0, 1, 0, position.y," +
                        "           0, 0, 1, 0         ," +
                        "           0, 0, 0, 1          " +
                        "   );" +
                        "   return m;" +
                        "}" +
                        "void main() {" +
                        "   texture_coord = vertex;" +
                        "   gl_Position = vec4(vertex, 0, 1);" +
                        "   gl_Position *= model();" +
                        "   gl_Position *= projection();" +
                        "}",
                "/* Fragment Shader */" +
                        "precision mediump float;" +
                        "uniform sampler2D texture;" +
                        "uniform vec2 card_coord;" +
                        "uniform float blue_tone;" +
                        "varying vec2 texture_coord;" +
                        "vec2 coord () {" +
                        "   vec4 v = vec4(texture_coord, 0, 1);" +
                        "   v *= mat4(" +
                        "       -1, 0, 0, 0," +
                        "        0, 1, 0, 0," +
                        "        0, 0, 1, 0," +
                        "        0, 0, 0, 1 " +
                        "   );" +
                        "   v *= mat4(" +
                        "       1, 0, 0, 0.890552," +
                        "       0, 1, 0, 0.634646," +
                        "       0, 0, 1, 0       ," +
                        "       0, 0, 0, 1        " +
                        "   );" +
                        "   v *= mat4(" +
                        "       0.561449, 0       , 0, 0," +
                        "       0       , 0.787840, 0, 0," +
                        "       0       , 0       , 1, 0," +
                        "       0       , 0       , 0, 1 " +
                        "   );" +
                        "   v *= mat4(" +
                        "       1, 0, 0, card_coord.x," +
                        "       0, 1, 0, card_coord.y," +
                        "       0, 0, 1, 0           ," +
                        "       0, 0, 0, 1            " +
                        "   );" +
                        "   v *= mat4(" +
                        "       0.1998355, 0        , 0, 0," +
                        "       0        , 0.0769158, 0, 0," +
                        "       0        , 0        , 1, 0," +
                        "       0        , 0        , 0, 1 " +
                        "   );" +
                        "   return v.xy;" +
                        "}" +
                        "void main() {" +
                        "   gl_FragColor = texture2D(texture, coord()) * vec4(1.0 - blue_tone, 1.0 - blue_tone, 1.0, 1.0);" +
                        "}",
                GL.GL_TRIANGLES, 0, cardData.getCount()
        );
        setAttribute("vertex", false, 0, 0);

        setTexture("texture", R.drawable.playing_cards);

        setObjectUniformNames("position", "card_coord", "blue_tone");
    }

    public void print(PlayingCards cards, int mode) {
        this.cards = cards;
        this.mode = mode;
        requestRender();
    }

    /**
     * Define o modo como as cartas serão inicialmente posicionadas.
     * Pode ser todas as cartas no centro da tela ou todas as cartas lado a lado.
     */
    private void changeMode() {
        List<GLObject> objects = getObjects();
        GLObject object;
        switch (mode) {
            case CENTERED:
                objects.clear();
                for (String card :
                        cards) {
                    object = new GLObject();
                    object.set("position", 0, 0);
                    object.set("card_coord", cardData.getCardCoord(card));
                    object.set("blue_tone", 0);
                    objects.add(object);
                }
                break;
            case SIDEBYSIDE:
                objects.clear();
                float x = - 1 / r_width + 0.634646f * 0.4f;
                float y = 1 / r_height - 0.890552f * 0.4f;
                for (String card :
                        cards) {
                    object = new GLObject();
                    object.set("position", x, y);
                    if (x <= 1 / r_width - 0.634646f * 0.4f) {
                        x += 0.634646f * 0.4f;
                    }
                    else {
                        x = - 1 / r_width + 0.634646f * 0.4f;
                        if (y >= - 1 / r_height + 0.890552f * 0.4f) {
                            y -= 0.890552f * 0.4f;
                        }
                        else {
                            y = 1 / r_height - 0.890552f * 0.4f;
                        }
                    }
                    object.set("card_coord", cardData.getCardCoord(card));
                    object.set("blue_tone", 0);
                    objects.add(object);
                }
                break;
        }
        this.mode = -1;
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        float ratio = (float) width / height;

        r_width = ratio > 1f ? 1f / ratio : 1f;
        r_height = ratio <= 1f ? ratio : 1f;

        setUniform("ratio", ratio);
        mode = ratio > 1 ? SIDEBYSIDE : CENTERED;
        changeMode();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, int width, int height) {
        boolean b;
        b = moveCardsEventHandler.onTouchEvent(event, width, height);
        b |= holdEventHandler.onTouchEvent(event, width, height);
        b |= flipCardsEventHandler.onTouchEvent(event, width, height);
        return b;
    }

    private boolean cardHit() {
        return v[0] * v[0] <= 0.890552f * 0.890552f && v[1] * v[1] <= 0.634646f * 0.634646f;
    }

    private void flipCard(GLObject card, int index) {
        if (cardData.getCardCoord("Back") == card.getFloats("card_coord")) {
            card.set("card_coord", cardData.getCardCoord(cards.get(index)));
        }
        else {
            card.set("card_coord", cardData.getCardCoord("Back"));
        }
    }

    /**
     * Multiplica rhsVec com a inversa da matriz m
     *  @param m float[16] representando uma matriz 4x4
     * @param rhsVec float[4] representando um vetor de dimenção 4
     */
    private void MultiplyInvMRhsVec(float[] m, float[] rhsVec) {
        // Criando uma matriz que transforma coordenadas da tela em coordenadas do modelo.
        Matrix.invertM(m, 0, m, 0);

        // Obtendo a variação do dedo nas coordenadas do modelo.
        Matrix.multiplyMV(v, 0, m, 0, rhsVec, 0);
    }

    private void positionUpdate(float[] position) {
        position[0] += v[0];
        position[1] += v[1];
    }

    private void overAll(int index) {
        List<GLObject> objects = getObjects();
        for (int i = index; i < objects.size()-1; i++) {
            Collections.swap(objects, i, i+1);
            Collections.swap(cards, i, i+1);
        }
    }

    /**
     * Transforma as coordenadas x, y em coordenadas do modelo da carta card
     *  @param x coordenada entre -1 1
     * @param y coordenada entre -1 1
     * @param card GLObject que representa carta e tem como atributos "position" e "card_coord"
     */
    private void setModelCoord(float x, float y, GLObject card) {
        // Pegando a posição da carta
        float[] position = card.getFloats("position");

        // Criando a matriz de transformação dos vértices da carta, idêntico ao do
        // shader
        setModelMatrix(position);

        // x, y são coordenadas da tela, m é uma matriz que transforma coordenadas do
        // modelo da carta em coordenadas da tela, por isso é necessário inverter a
        // matriz.
        MultiplyInvMRhsVec(m, new float[] {x, y, 0, 1});
    }

    private void setModelMatrix(float[] position) {
        setProjectionMatrix();
        Matrix.translateM(m, 0, position[0], position[1], 1);
        Matrix.rotateM(m, 0, 90f, 0, 0, 1f);
        Matrix.scaleM(m, 0, 0.4f, 0.4f, 1);
    }

    private void setProjectionMatrix() {
        Matrix.setIdentityM(m, 0);
        Matrix.scaleM(m, 0, r_width, r_height, 1);
    }

    private class CardData {
        private HashMap<String, float[]> cardImage = new HashMap<>();
        private float[] array;
        private short[] elementArray;
        private int count;

        private CardData() {
            array = new float[] {
                    -0.890552f, 0.634646f,
                    0.890552f, 0.634646f,
                    -0.890552f, -0.634646f,
                    0.890552f, -0.634646f
            };

            elementArray = new short[] {
                    0, 1, 2,
                    2, 3, 1
            };

            count = elementArray.length;

            cardImage.put("Joker Black", new float[] {0f, 0f});
            cardImage.put("As", new float[] {1f, 0f});
            cardImage.put("Ah", new float[] {2f, 0f});
            cardImage.put("Ad", new float[] {3f, 0f});
            cardImage.put("Ac", new float[] {4f, 0f});
            cardImage.put("Joker Red", new float[] {0f, 1f});
            cardImage.put("2s", new float[] {1f, 1f});
            cardImage.put("2h", new float[] {2f, 1f});
            cardImage.put("2d", new float[] {3f, 1f});
            cardImage.put("2c", new float[] {4f, 1f});
            cardImage.put("Back", new float[] {0f, 2f});
            cardImage.put("3s", new float[] {1f, 2f});
            cardImage.put("3h", new float[] {2f, 2f});
            cardImage.put("3d", new float[] {3f, 2f});
            cardImage.put("3c", new float[] {4f, 2f});
            cardImage.put("4s", new float[] {1f, 3f});
            cardImage.put("4h", new float[] {2f, 3f});
            cardImage.put("4d", new float[] {3f, 3f});
            cardImage.put("4c", new float[] {4f, 3f});
            cardImage.put("5s", new float[] {1f, 4f});
            cardImage.put("5h", new float[] {2f, 4f});
            cardImage.put("5d", new float[] {3f, 4f});
            cardImage.put("5c", new float[] {4f, 4f});
            cardImage.put("6s", new float[] {1f, 5f});
            cardImage.put("6h", new float[] {2f, 5f});
            cardImage.put("6d", new float[] {3f, 5f});
            cardImage.put("6c", new float[] {4f, 5f});
            cardImage.put("7s", new float[] {1f, 6f});
            cardImage.put("7h", new float[] {2f, 6f});
            cardImage.put("7d", new float[] {3f, 6f});
            cardImage.put("7c", new float[] {4f, 6f});
            cardImage.put("8s", new float[] {1f, 7f});
            cardImage.put("8h", new float[] {2f, 7f});
            cardImage.put("8d", new float[] {3f, 7f});
            cardImage.put("8c", new float[] {4f, 7f});
            cardImage.put("9s", new float[] {1f, 8f});
            cardImage.put("9h", new float[] {2f, 8f});
            cardImage.put("9d", new float[] {3f, 8f});
            cardImage.put("9c", new float[] {4f, 8f});
            cardImage.put("Ts", new float[] {1f, 9f});
            cardImage.put("Th", new float[] {2f, 9f});
            cardImage.put("Td", new float[] {3f, 9f});
            cardImage.put("Tc", new float[] {4f, 9f});
            cardImage.put("Js", new float[] {1f, 10f});
            cardImage.put("Jh", new float[] {2f, 10f});
            cardImage.put("Jd", new float[] {3f, 10f});
            cardImage.put("Jc", new float[] {4f, 10f});
            cardImage.put("Qs", new float[] {1f, 11f});
            cardImage.put("Qh", new float[] {2f, 11f});
            cardImage.put("Qd", new float[] {3f, 11f});
            cardImage.put("Qc", new float[] {4f, 11f});
            cardImage.put("Ks", new float[] {1f, 12f});
            cardImage.put("Kh", new float[] {2f, 12f});
            cardImage.put("Kd", new float[] {3f, 12f});
            cardImage.put("Kc", new float[] {4f, 12f});
        }

        public float[] getArray() {
            return array;
        }

        public short[] getElementArray() {
            return elementArray;
        }

        public int getCount() {
            return count;
        }

        public float[] getCardCoord(String cardName) {
            if (!cardImage.containsKey(cardName)) {
                throw new RuntimeException(cardName + " não é nome de carta válido!");
            }
            return cardImage.get(cardName);
        }
    }
}
