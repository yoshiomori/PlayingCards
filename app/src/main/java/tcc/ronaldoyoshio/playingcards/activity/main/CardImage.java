package tcc.ronaldoyoshio.playingcards.activity.main;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.HashMap;

import tcc.ronaldoyoshio.playingcards.GL.GL;
import tcc.ronaldoyoshio.playingcards.GL.GLImage;
import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;

/**
 * Desenhando uma carta de baralho
 * Created by mori on 15/07/16.
 */
public class CardImage extends GLImage {
    private final CardData cardData;
    private ArrayList<GLObject> selectCards = new ArrayList<>();
    private float[] invMMVPMatrix;
    private float left = 0;
    private float right = 0;
    private float bottom = 0;
    private float top = 0;

    public CardImage() {
        cardData = new CardData();
        setArray(cardData.getArray());
        setElementArray(cardData.getElementArray());
        setShader(
                "/* Vertex Shader */" +
                        "attribute vec3 vertex;" +
                        "uniform float right, left, top, bottom, near, far;" +
                        "uniform vec3 eye, center, up;" +
                        "uniform vec2 card_coord, position;" +
                        "varying vec2 texture_coord;" +
                        "mat4 frustum() {" +
                        "   float r_width  = 1.0 / (right - left);" +
                        "   float r_height = 1.0 / (top - bottom);" +
                        "   float r_depth  = 1.0 / (near - far);" +
                        "   float x = 2.0 * (near * r_width);" +
                        "   float y = 2.0 * (near * r_height);" +
                        "   float A = (right + left) * r_width;" +
                        "   float B = (top + bottom) * r_height;" +
                        "   float C = (far + near) * r_depth;" +
                        "   float D = 2.0 * (far * near * r_depth);" +
                        "   return mat4(x, 0, 0, 0, 0, y, 0, 0, A, B, C, -1, 0, 0, D, 0);" +
                        "}" +
                        "mat4 look_at() {" +
                        "   vec3 f, s, u;" +
                        "" +
                        "   f = normalize(vec3(center - eye));" +
                        "" +
                        "   s = normalize(cross(f, up));" +
                        "" +
                        "   u = cross(s, f);" +
                        "   return mat4(s.x, u.x, -f.x, 0," +
                        "               s.y, u.y, -f.y, 0," +
                        "               s.z, u.z, -f.z, 0," +
                        "               0, 0, 0, 1)" +
                        "       * mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -eye, 1);" +
                        "}" +
                        "void main() {" +
                        "   vec4 v = vec4(vertex, 1);" +
                        "   v = mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0.890552, 0.634646, 0, 1) * v;" +
                        "   v = mat4(0.561449, 0, 0, 0, 0, 0.787840, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1) * v;" +
                        "   v = mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, card_coord, 0, 1) * v;" +
                        "   v = mat4(0.1998355, 0, 0, 0, 0, 0.0769158, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1) * v;" +
                        "   texture_coord = v.xy;" +
                        "   gl_Position = frustum() * look_at() * mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, position, 0, 1) * vec4(vertex, 1);" +
                        "}",
                "/* Fragment Shader */" +
                        "precision mediump float;" +
                        "uniform sampler2D texture;" +
                        "varying vec2 texture_coord;" +
                        "void main() {" +
                        "   gl_FragColor = texture2D(texture, texture_coord);" +
                        "}",
                GL.GL_TRIANGLES, 0, cardData.getCount()
        );
        setAttribute("vertex", false, 0, 0);

        setScreen("left", "right", "bottom", "top");
        setUniform("near", 3f);
        setUniform("far", 7f);

        setUniform("eye", 0f, 0f, -6f);
        setUniform("center", 0f, 0f, 0f);
        setUniform("up", 0f, 1f, 0f);

        setTexture("texture", R.drawable.playing_cards);

        setPositionName("position");
        setColorName("card_coord");
    }

    protected void print(PlayingCards cards) {
        ArrayList<GLObject> thisCards = getObjects();
        thisCards.clear();
        for (String card :
                cards) {
            addObject(new float[] {0, 0}, cardData.getCardCoord(card));
        }
    }

    @Override
    public void onMove(float dx, float dy) {
        if (left != getLeft() || right != getRight() || bottom != getBottom() || top != getTop()) {
            left = getLeft();
            right = getRight();
            bottom = getBottom();
            top = getTop();
            invMMVPMatrix = new float[16];
            float[] mProjectionMatrix = new float[16];
            Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, 3, 7);
            float[] mViewMatrix = new float[16];
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            float[] mMVPMatrix = new float[16];
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            Matrix.invertM(invMMVPMatrix, 0, mMVPMatrix, 0);
        }
        float[] d = new float[4];
        Matrix.multiplyMV(d, 0, invMMVPMatrix, 0, new float[]{dx, dy, 0, 0}, 0);
        for (GLObject card : selectCards) {
            float[] position = card.getPosition();
            card.setPosition(position[0] + d[0] * 6f, position[1] + d[1] * 6f);
        }
    }

    @Override
    public void onDown(float x, float y) {
        if (left != getLeft() || right != getRight() || bottom != getBottom() || top != getTop()) {
            left = getLeft();
            right = getRight();
            bottom = getBottom();
            top = getTop();
            invMMVPMatrix = new float[16];
            float[] mProjectionMatrix = new float[16];
            Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, 3, 7);
            float[] mViewMatrix = new float[16];
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            float[] mMVPMatrix = new float[16];
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            Matrix.invertM(invMMVPMatrix, 0, mMVPMatrix, 0);
        }
        float[] v = new float[4];
        Matrix.multiplyMV(v, 0, invMMVPMatrix, 0, new float[]{x, y, 0, 1}, 0);
        ArrayList<GLObject> cards = getObjects();
        for(int index = cards.size() - 1; index >= 0; index--) {
            if (!selectCards.contains(cards.get(index))) {
                float[] position = cards.get(index).getPosition();
                if (Math.abs(position[0] - v[0] * 6f) <= 0.890552f
                        && Math.abs(position[1] - v[1] * 6f ) <= 0.634646f) {
                    selectCards.add(cards.get(index));
                    break;
                }
            }
        }

    }

    @Override
    public void onUp() {
        selectCards.clear();
    }

    private class CardData {
        private HashMap<String, float[]> cardImage = new HashMap<>();
        private float[] array;
        private short[] elementArray;
        private int count;

        private CardData() {
            array = new float[] {
                    0.890552f, 0.573229f, 0.000000f,
                    0.888928f, 0.587302f, 0.002047f,
                    0.890552f, 0.573229f, 0.002047f,
                    0.888928f, 0.587302f, 0.000000f,
                    0.884304f, 0.600225f, 0.002047f,
                    0.888928f, 0.587302f, 0.002047f,
                    0.888928f, 0.587302f, 0.000000f,
                    0.884304f, 0.600225f, 0.000000f,
                    0.877050f, 0.611629f, 0.002047f,
                    0.884304f, 0.600225f, 0.002047f,
                    0.884304f, 0.600225f, 0.000000f,
                    0.877050f, 0.611629f, 0.000000f,
                    0.867535f, 0.621144f, 0.002047f,
                    0.877050f, 0.611629f, 0.002047f,
                    0.877050f, 0.611629f, 0.000000f,
                    0.877050f, 0.611629f, 0.000000f,
                    0.867535f, 0.621144f, 0.000000f,
                    0.856131f, 0.628398f, 0.002047f,
                    0.867535f, 0.621144f, 0.002047f,
                    0.867535f, 0.621144f, 0.000000f,
                    0.856131f, 0.628398f, 0.000000f,
                    0.843207f, 0.633023f, 0.002047f,
                    0.856131f, 0.628398f, 0.002047f,
                    0.856131f, 0.628398f, 0.000000f,
                    0.843207f, 0.633023f, 0.000000f,
                    0.829135f, 0.634646f, 0.002047f,
                    0.843207f, 0.633023f, 0.002047f,
                    0.843207f, 0.633023f, 0.000000f,
                    0.829135f, 0.634646f, 0.000000f,
                    -0.829135f, 0.634646f, 0.002047f,
                    0.829135f, 0.634646f, 0.002047f,
                    0.829135f, 0.634646f, 0.000000f,
                    -0.829135f, 0.634646f, 0.000000f,
                    -0.843207f, 0.633023f, 0.002047f,
                    -0.829135f, 0.634646f, 0.002047f,
                    -0.829135f, 0.634646f, 0.000000f,
                    -0.843207f, 0.633023f, 0.000000f,
                    -0.856131f, 0.628398f, 0.002047f,
                    -0.843207f, 0.633023f, 0.002047f,
                    -0.843207f, 0.633023f, 0.000000f,
                    -0.856131f, 0.628398f, 0.000000f,
                    -0.867535f, 0.621144f, 0.002047f,
                    -0.856131f, 0.628398f, 0.002047f,
                    -0.856131f, 0.628398f, 0.000000f,
                    -0.867535f, 0.621144f, 0.000000f,
                    -0.877050f, 0.611629f, 0.002047f,
                    -0.867535f, 0.621144f, 0.002047f,
                    -0.867535f, 0.621144f, 0.000000f,
                    -0.877050f, 0.611629f, 0.000000f,
                    -0.884304f, 0.600225f, 0.002047f,
                    -0.877050f, 0.611629f, 0.002047f,
                    -0.877050f, 0.611629f, 0.000000f,
                    -0.884304f, 0.600225f, 0.000000f,
                    -0.888928f, 0.587302f, 0.002047f,
                    -0.884304f, 0.600225f, 0.002047f,
                    -0.884304f, 0.600225f, 0.000000f,
                    -0.884304f, 0.600225f, 0.000000f,
                    -0.888928f, 0.587302f, 0.000000f,
                    -0.890552f, 0.573229f, 0.002047f,
                    -0.888928f, 0.587302f, 0.002047f,
                    -0.888928f, 0.587302f, 0.000000f,
                    -0.890552f, 0.573229f, 0.000000f,
                    -0.890552f, -0.573229f, 0.002047f,
                    -0.890552f, 0.573229f, 0.002047f,
                    -0.890552f, 0.573229f, 0.000000f,
                    -0.890552f, -0.573229f, 0.000000f,
                    -0.888928f, -0.587302f, 0.002047f,
                    -0.890552f, -0.573229f, 0.002047f,
                    -0.890552f, -0.573229f, 0.000000f,
                    -0.888928f, -0.587302f, 0.000000f,
                    -0.884304f, -0.600225f, 0.002047f,
                    -0.888928f, -0.587302f, 0.002047f,
                    -0.888928f, -0.587302f, 0.000000f,
                    -0.884304f, -0.600225f, 0.000000f,
                    -0.877050f, -0.611629f, 0.002047f,
                    -0.884304f, -0.600225f, 0.002047f,
                    -0.884304f, -0.600225f, 0.000000f,
                    -0.877050f, -0.611629f, 0.000000f,
                    -0.867535f, -0.621144f, 0.002047f,
                    -0.877050f, -0.611629f, 0.002047f,
                    -0.877050f, -0.611629f, 0.000000f,
                    -0.877050f, -0.611629f, 0.000000f,
                    -0.867535f, -0.621144f, 0.000000f,
                    -0.856131f, -0.628398f, 0.002047f,
                    -0.867535f, -0.621144f, 0.002047f,
                    -0.867535f, -0.621144f, 0.000000f,
                    -0.856131f, -0.628398f, 0.000000f,
                    -0.843207f, -0.633023f, 0.002047f,
                    -0.856131f, -0.628398f, 0.002047f,
                    -0.856131f, -0.628398f, 0.000000f,
                    -0.843207f, -0.633023f, 0.000000f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.843207f, -0.633023f, 0.002047f,
                    -0.843207f, -0.633023f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.829135f, -0.634646f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.829135f, -0.634646f, 0.000000f,
                    0.843207f, -0.633023f, 0.002047f,
                    0.829135f, -0.634646f, 0.002047f,
                    0.829135f, -0.634646f, 0.000000f,
                    0.843207f, -0.633023f, 0.000000f,
                    0.856131f, -0.628398f, 0.002047f,
                    0.843207f, -0.633023f, 0.002047f,
                    0.843207f, -0.633023f, 0.000000f,
                    0.856131f, -0.628398f, 0.000000f,
                    0.867535f, -0.621144f, 0.002047f,
                    0.856131f, -0.628398f, 0.002047f,
                    0.856131f, -0.628398f, 0.000000f,
                    0.867535f, -0.621144f, 0.000000f,
                    0.877050f, -0.611629f, 0.002047f,
                    0.867535f, -0.621144f, 0.002047f,
                    0.867535f, -0.621144f, 0.000000f,
                    0.877050f, -0.611629f, 0.000000f,
                    0.884304f, -0.600225f, 0.002047f,
                    0.877050f, -0.611629f, 0.002047f,
                    0.877050f, -0.611629f, 0.000000f,
                    0.884304f, -0.600225f, 0.000000f,
                    0.888928f, -0.587302f, 0.002047f,
                    0.884304f, -0.600225f, 0.002047f,
                    0.884304f, -0.600225f, 0.000000f,
                    0.884304f, -0.600225f, 0.000000f,
                    0.888928f, -0.587302f, 0.000000f,
                    0.890552f, -0.573229f, 0.002047f,
                    0.888928f, -0.587302f, 0.002047f,
                    0.888928f, -0.587302f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.890552f, 0.573229f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.890552f, 0.573229f, 0.000000f,
                    -0.856131f, -0.628398f, 0.000000f,
                    -0.867535f, -0.621144f, 0.000000f,
                    -0.877050f, -0.611629f, 0.000000f,
                    -0.856131f, -0.628398f, 0.000000f,
                    -0.877050f, -0.611629f, 0.000000f,
                    -0.884304f, -0.600225f, 0.000000f,
                    -0.856131f, -0.628398f, 0.000000f,
                    -0.884304f, -0.600225f, 0.000000f,
                    -0.888928f, -0.587302f, 0.000000f,
                    -0.843207f, -0.633023f, 0.000000f,
                    -0.856131f, -0.628398f, 0.000000f,
                    -0.888928f, -0.587302f, 0.000000f,
                    -0.843207f, -0.633023f, 0.000000f,
                    -0.888928f, -0.587302f, 0.000000f,
                    -0.890552f, -0.573229f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.843207f, -0.633023f, 0.000000f,
                    -0.890552f, -0.573229f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.890552f, -0.573229f, 0.000000f,
                    -0.890552f, 0.573229f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.890552f, 0.573229f, 0.000000f,
                    -0.888928f, 0.587302f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.888928f, 0.587302f, 0.000000f,
                    -0.884304f, 0.600225f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.884304f, 0.600225f, 0.000000f,
                    -0.877050f, 0.611629f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.877050f, 0.611629f, 0.000000f,
                    -0.867535f, 0.621144f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.867535f, 0.621144f, 0.000000f,
                    -0.856131f, 0.628398f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.856131f, 0.628398f, 0.000000f,
                    -0.843207f, 0.633023f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.843207f, 0.633023f, 0.000000f,
                    -0.829135f, 0.634646f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    -0.829135f, 0.634646f, 0.000000f,
                    0.829135f, 0.634646f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.829135f, 0.634646f, 0.000000f,
                    0.843207f, 0.633023f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.843207f, 0.633023f, 0.000000f,
                    0.856131f, 0.628398f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.856131f, 0.628398f, 0.000000f,
                    0.867535f, 0.621144f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.867535f, 0.621144f, 0.000000f,
                    0.877050f, 0.611629f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.877050f, 0.611629f, 0.000000f,
                    0.884304f, 0.600225f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.884304f, 0.600225f, 0.000000f,
                    0.888928f, 0.587302f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.888928f, 0.587302f, 0.000000f,
                    0.890552f, 0.573229f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.890552f, 0.573229f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.829135f, -0.634646f, 0.000000f,
                    -0.829135f, -0.634646f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.843207f, -0.633023f, 0.000000f,
                    0.829135f, -0.634646f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.856131f, -0.628398f, 0.000000f,
                    0.843207f, -0.633023f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.867535f, -0.621144f, 0.000000f,
                    0.856131f, -0.628398f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.877050f, -0.611629f, 0.000000f,
                    0.867535f, -0.621144f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.884304f, -0.600225f, 0.000000f,
                    0.877050f, -0.611629f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.884304f, -0.600225f, 0.000000f,
                    0.890552f, -0.573229f, 0.000000f,
                    0.888928f, -0.587302f, 0.000000f,
                    0.884304f, -0.600225f, 0.002047f,
                    0.888928f, -0.587302f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    -0.877050f, -0.611629f, 0.002047f,
                    -0.867535f, -0.621144f, 0.002047f,
                    -0.856131f, -0.628398f, 0.002047f,
                    -0.884304f, -0.600225f, 0.002047f,
                    -0.877050f, -0.611629f, 0.002047f,
                    -0.856131f, -0.628398f, 0.002047f,
                    -0.888928f, -0.587302f, 0.002047f,
                    -0.884304f, -0.600225f, 0.002047f,
                    -0.856131f, -0.628398f, 0.002047f,
                    -0.888928f, -0.587302f, 0.002047f,
                    -0.856131f, -0.628398f, 0.002047f,
                    -0.843207f, -0.633023f, 0.002047f,
                    -0.890552f, -0.573229f, 0.002047f,
                    -0.888928f, -0.587302f, 0.002047f,
                    -0.843207f, -0.633023f, 0.002047f,
                    -0.890552f, -0.573229f, 0.002047f,
                    -0.843207f, -0.633023f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.890552f, 0.573229f, 0.002047f,
                    -0.890552f, -0.573229f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.888928f, 0.587302f, 0.002047f,
                    -0.890552f, 0.573229f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.884304f, 0.600225f, 0.002047f,
                    -0.888928f, 0.587302f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.877050f, 0.611629f, 0.002047f,
                    -0.884304f, 0.600225f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.867535f, 0.621144f, 0.002047f,
                    -0.877050f, 0.611629f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.856131f, 0.628398f, 0.002047f,
                    -0.867535f, 0.621144f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.843207f, 0.633023f, 0.002047f,
                    -0.856131f, 0.628398f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    -0.829135f, 0.634646f, 0.002047f,
                    -0.843207f, 0.633023f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.829135f, 0.634646f, 0.002047f,
                    -0.829135f, 0.634646f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.843207f, 0.633023f, 0.002047f,
                    0.829135f, 0.634646f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.856131f, 0.628398f, 0.002047f,
                    0.843207f, 0.633023f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.867535f, 0.621144f, 0.002047f,
                    0.856131f, 0.628398f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.877050f, 0.611629f, 0.002047f,
                    0.867535f, 0.621144f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.884304f, 0.600225f, 0.002047f,
                    0.877050f, 0.611629f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.888928f, 0.587302f, 0.002047f,
                    0.884304f, 0.600225f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.890552f, 0.573229f, 0.002047f,
                    0.888928f, 0.587302f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    0.890552f, 0.573229f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    -0.829135f, -0.634646f, 0.002047f,
                    0.829135f, -0.634646f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    0.829135f, -0.634646f, 0.002047f,
                    0.843207f, -0.633023f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    0.843207f, -0.633023f, 0.002047f,
                    0.856131f, -0.628398f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    0.856131f, -0.628398f, 0.002047f,
                    0.867535f, -0.621144f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    0.867535f, -0.621144f, 0.002047f,
                    0.877050f, -0.611629f, 0.002047f,
                    0.884304f, -0.600225f, 0.002047f,
                    0.890552f, -0.573229f, 0.002047f,
                    0.877050f, -0.611629f, 0.002047f};

            elementArray = new short[] {
                    3, 0, 1, 2,
                    3, 0, 3, 1,
                    3, 4, 5, 6,
                    3, 6, 7, 4,
                    3, 8, 9, 10,
                    3, 10, 11, 8,
                    3, 12, 13, 14,
                    3, 15, 16, 12,
                    3, 17, 18, 19,
                    3, 19, 20, 17,
                    3, 21, 22, 23,
                    3, 23, 24, 21,
                    3, 25, 26, 27,
                    3, 27, 28, 25,
                    3, 29, 30, 31,
                    3, 31, 32, 29,
                    3, 33, 34, 35,
                    3, 35, 36, 33,
                    3, 37, 38, 39,
                    3, 39, 40, 37,
                    3, 41, 42, 43,
                    3, 43, 44, 41,
                    3, 45, 46, 47,
                    3, 47, 48, 45,
                    3, 49, 50, 51,
                    3, 51, 52, 49,
                    3, 53, 54, 55,
                    3, 56, 57, 53,
                    3, 58, 59, 60,
                    3, 60, 61, 58,
                    3, 62, 63, 64,
                    3, 64, 65, 62,
                    3, 66, 67, 68,
                    3, 68, 69, 66,
                    3, 70, 71, 72,
                    3, 72, 73, 70,
                    3, 74, 75, 76,
                    3, 76, 77, 74,
                    3, 78, 79, 80,
                    3, 81, 82, 78,
                    3, 83, 84, 85,
                    3, 85, 86, 83,
                    3, 87, 88, 89,
                    3, 89, 90, 87,
                    3, 91, 92, 93,
                    3, 93, 94, 91,
                    3, 95, 96, 97,
                    3, 97, 98, 95,
                    3, 99, 100, 101,
                    3, 101, 102, 99,
                    3, 103, 104, 105,
                    3, 105, 106, 103,
                    3, 107, 108, 109,
                    3, 109, 110, 107,
                    3, 111, 112, 113,
                    3, 113, 114, 111,
                    3, 115, 116, 117,
                    3, 117, 118, 115,
                    3, 119, 120, 121,
                    3, 122, 123, 119,
                    3, 124, 125, 126,
                    3, 126, 127, 124,
                    3, 128, 129, 130,
                    3, 130, 131, 128,
                    3, 132, 133, 134,
                    3, 135, 136, 137,
                    3, 138, 139, 140,
                    3, 141, 142, 143,
                    3, 144, 145, 146,
                    3, 147, 148, 149,
                    3, 150, 151, 152,
                    3, 153, 154, 155,
                    3, 156, 157, 158,
                    3, 159, 160, 161,
                    3, 162, 163, 164,
                    3, 165, 166, 167,
                    3, 168, 169, 170,
                    3, 171, 172, 173,
                    3, 174, 175, 176,
                    3, 177, 178, 179,
                    3, 180, 181, 182,
                    3, 183, 184, 185,
                    3, 186, 187, 188,
                    3, 189, 190, 191,
                    3, 192, 193, 194,
                    3, 195, 196, 197,
                    3, 198, 199, 200,
                    3, 201, 202, 203,
                    3, 204, 205, 206,
                    3, 207, 208, 209,
                    3, 210, 211, 212,
                    3, 213, 214, 215,
                    3, 216, 217, 218,
                    3, 219, 220, 221,
                    3, 222, 223, 224,
                    3, 225, 226, 227,
                    3, 228, 229, 230,
                    3, 231, 232, 233,
                    3, 234, 235, 236,
                    3, 237, 238, 239,
                    3, 240, 241, 242,
                    3, 243, 244, 245,
                    3, 246, 247, 248,
                    3, 249, 250, 251,
                    3, 252, 253, 254,
                    3, 255, 256, 257,
                    3, 258, 259, 260,
                    3, 261, 262, 263,
                    3, 264, 265, 266,
                    3, 267, 268, 269,
                    3, 270, 271, 272,
                    3, 273, 274, 275,
                    3, 276, 277, 278,
                    3, 279, 280, 281,
                    3, 282, 283, 284,
                    3, 285, 286, 287,
                    3, 288, 289, 290,
                    3, 291, 292, 293,
                    3, 294, 295, 296,
                    3, 297, 298, 299,
                    3, 300, 301, 302,
                    3, 303, 304, 305,
                    3, 306, 307, 308,
                    3, 309, 310, 311};

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
