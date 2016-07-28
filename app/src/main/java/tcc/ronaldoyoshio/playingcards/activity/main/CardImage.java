package tcc.ronaldoyoshio.playingcards.activity.main;

import android.opengl.Matrix;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
    private boolean doubleTap = false;
    private EventHandler eventHandler = new EventHandler(){
        private HashMap<Integer, GLObject> pointerCards = new HashMap<>();
        @Override
        public boolean onDown(int pointerId, float x, float y, int width, int height) {
            findObject(pointerId, getGLX(x, width), getGLY(y, height));
            return true;
        }

        @Override
        public boolean onMove(int pointerId, float dx, float dy, int width, int height) {
            // Criando a matriz de projeção do modelo para a tela, idêntico ao do shader.
            setProjectionMatrix();
            setModelCoord(m, v, new float[]{getGLDx(dx, width), getGLDy(dy, height), 0, 0});

            if (pointerCards.containsKey(pointerId)) {
                GLObject card = pointerCards.get(pointerId);
                positionUpdate(card.getFloats("position"));
            }

            return true;
        }


        @Override
        public boolean onUp(int pointerId) {
            pointerCards.clear();
            return true;
        }

        @Override
        public boolean onDoubleTap() {
            doubleTap = true;
            return true;
        }

        @Override
        public boolean onPointerDown(int pointerId, float x, float y, int width, int height) {
            // rX, rY é a posição do dedo nas coordenadas da tela
            findObject(pointerId, getGLX(x, width), getGLY(y, height));
            return true;
        }

        private void findObject(int pointerId, float x, float y) {
            ArrayList<GLObject> objects = getObjects();

            // Procurando a última das cartas que contém o ponto x, y
            for (int index = objects.size() - 1; index >= 0; index--) {
                GLObject card = getGlObject(x, y, index);

                // Se o ponto x, y está contido no modelo da carta então o ponto foi encontrado
                // e o laço é quebrado.
                if (cardHit()) {
                    if (doubleTap) {
                        doubleTap = false;
                        flipCard(card, index);
                    }
                    pointerCards.put(pointerId, card);
                    overAll(index);
                    break;
                }
            }
        }

        @Override
        public boolean onPointerUp(int pointerId) {
            pointerCards.remove(pointerId);
            return true;
        }
    };

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
                        "   gl_FragColor = texture2D(texture, coord());" +
                        "}",
                GL.GL_TRIANGLES, 0, cardData.getCount()
        );
        setAttribute("vertex", false, 0, 0);

        setTexture("texture", R.drawable.playing_cards);

        setObjectUniformNames("position", "card_coord");
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
        ArrayList<GLObject> objects = getObjects();
        GLObject object;
        switch (mode) {
            case CENTERED:
                objects.clear();
                for (String card :
                        cards) {
                    object = new GLObject();
                    object.set("position", 0, 0);
                    object.set("card_coord", cardData.getCardCoord(card));
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
        return eventHandler.onTouchEvent(event, width, height);
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

    private void setModelCoord(float[] m, float[] v, float[] rhsVec) {
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
        ArrayList<GLObject> objects = getObjects();
        for (int i = index; i < objects.size()-1; i++) {
            Collections.swap(objects, i, i+1);
            Collections.swap(cards, i, i+1);
        }
    }

    private GLObject getGlObject(float x, float y, int index) {
        ArrayList<GLObject> objects = getObjects();

        // Pegando a carta
        GLObject card = objects.get(index);

        // Pegando a posição da carta
        float[] position = card.getFloats("position");

        // Criando a matriz de transformação dos vértices da carta, idêntico ao do
        // shader
        setModelMatrix(position);

        // x, y são coordenadas da tela, m é uma matriz que transforma coordenadas do
        // modelo da carta em coordenadas da tela, por isso é necessário inverter a
        // matriz.
        setModelCoord(m, v, new float[] {x, y, 0, 1});
        return card;
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
