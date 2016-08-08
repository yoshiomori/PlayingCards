package tcc.ronaldoyoshio.playingcards.activity.select;

import android.content.Intent;

import tcc.ronaldoyoshio.playingcards.gl.GL;
import tcc.ronaldoyoshio.playingcards.gl.GLImage;
import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;
import tcc.ronaldoyoshio.playingcards.activity.deck.DeckActivity;
import tcc.ronaldoyoshio.playingcards.model.Hand;

/**
 * Botão de proseguir usado no SelectCardsActivity
 * Created by mori on 01/08/16.
 */
public class SelectButtonImage extends GLImage {

    public SelectButtonImage(final SelectCardsActivity selectCardsActivity, final Hand cards) {
        addTouchEventHandler(new TouchEventHandler() {
            @Override
            public boolean onDown(int pointerId, float x, float y, int width, int height) {
                float glx = (getGLX(x, width) - 1) * (width > height ? (float) width / height : 1) * 4f + 1;
                float gly = (getGLY(y, height) + 1) * (width > height ? 1 : height / width) * 4f - 0.294f;
                if (-1 < glx && glx < 1 && -0.294 < gly && gly < 0.294) {
                    Intent intent = new Intent(selectCardsActivity, DeckActivity.class);
                    intent.putExtra("cards", cards);
                    selectCardsActivity.startActivity(intent);
                }
                return super.onDown(pointerId, x, y, width, height);
            }
        });
    }

    @Override
    protected void onSurfaceCreated() {
        setShader(
                "/* VertexShader */" +
                        "attribute vec2 vertex;" +
                        "uniform float ratio;" +
                        "varying vec2 coord;" +
                        "void main() {" +
                        "   coord = vertex;" +
                        "   vec2 v = vertex + vec2(-1.0, 0.294);" +
                        "   v *= mat2(ratio > 1.0 ? 1.0/ratio : 1.0, 0.0, 0.0, ratio <= 1.0 ? ratio : 1.0);" +
                        "   v *= mat2(0.25, 0.0, 0.0, 0.25);" +
                        "   v += vec2(1, -1);" +
                        "   gl_Position = vec4(v, 0, 1);" +
                        "}",
                "/* FragmentShader */" +
                        "precision mediump float;" +
                        "uniform sampler2D texture;" +
                        "varying vec2 coord;" +
                        "void main() {" +
                        "   /* Escalando */" +
                        "   vec2 v = coord;" +
                        "   v *= mat2(0.5, 0.0, 0.0, - 0.5 / 0.294);" +
                        "   /* Transladando */" +
                        "   v += vec2(0.5, 0.5);" +
                        "   gl_FragColor = texture2D(texture, v);" +
                        "}",
                GL.GL_TRIANGLES, 6, new short[]{0, 1, 2, 2, 1, 3}
        );
        setAttribute(
                "vertex", false, 0,
                -1, 0.294f,
                1, 0.294f,
                -1, -0.294f,
                1, -0.294f
        );
        setTexture("texture", R.drawable.next);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        setUniform("ratio", (float) width / height);
    }
}
