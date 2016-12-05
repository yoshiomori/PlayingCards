package tcc.ronaldoyoshio.playingcards.images;

import java.util.HashMap;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.gl.GL;
import tcc.ronaldoyoshio.playingcards.gl.GLImage;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.model.Cards;

/**
 * Imagem de carta sem o tratamento de eventos.
 */
public class CardImage extends GLImage {
    private int totalCards; /* Número total de cartas necessário para poder embaralhar */
    public static final int SIDEBYSIDE = 1;
    public static final int CENTERED = 0;
    private CardData cardData = new CardData();
    private Cards cards = new Cards();
    private int mode;
    private float r_width;
    private float r_height;

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

    public void setCards(Cards cards) {
        this.cards = cards;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Cards getCards() {
        return cards;
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
        changeMode();
    }

    public int getTotalCards() {
        return totalCards;
    }

    public CardData getCardData() {
        return cardData;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public class CardData {
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

        float[] getArray() {
            return array;
        }

        short[] getElementArray() {
            return elementArray;
        }

        int getCount() {
            return count;
        }

        public float[] getCardCoord(String cardName) {
            if (!cardImage.containsKey(cardName)) {
                throw new RuntimeException(cardName + " não é nome de carta válido!");
            }
            return cardImage.get(cardName);
        }
    }

    public void addCard(String cardName, boolean upsidedown) {
        cards.add(cardName);
        GLObject object = new GLObject();
        object.set("position", 0, 0);
        object.set("card_coord", upsidedown ? cardData.getCardCoord("Back")
                : cardData.getCardCoord(cardName));
        object.set("blue_tone", 0);
        getObjects().add(object);
    }
}
