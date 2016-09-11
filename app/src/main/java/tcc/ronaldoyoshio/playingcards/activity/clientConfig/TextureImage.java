package tcc.ronaldoyoshio.playingcards.activity.clientConfig;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;

import tcc.ronaldoyoshio.playingcards.gl.GL;
import tcc.ronaldoyoshio.playingcards.gl.GLImage;

/**
 * Responsável por desenhar texturas
 * Created by mori on 10/09/16.
 */
public class TextureImage extends GLImage {
    Bitmap texture;

    public TextureImage(String text, int size) {
        TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        paint.setTextSize(size);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(TextPaint.Align.CENTER);
        paint.setTextScaleX(0.5f);
        int w = (int) paint.measureText(text);
        int h = (int) (paint.descent() - paint.ascent());
        texture = Bitmap.createBitmap(w * 2, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(texture);
        c.drawText(text, w, - paint.ascent(), paint);
    }

    @Override
    protected void onSurfaceCreated() {
        setArray(
                - 1f, 1f,
                1f, 1f,
                - 1f, - 1f,
                1f, - 1f
        );
        setElementArray(new short[] {0, 1, 2, 1, 2, 3});
        setShader(
                "/* Vertex Shader */" +
                        "attribute vec2 vertex;" +
                        "varying vec2 tex_coord;" +
                        "uniform float h, w;" +
                        "void main() {" +
                        "   tex_coord = vertex;" +
                        "   vec2 v = mat2(w, 0.0, 0.0, h) * vertex;" +
                        "   gl_Position = vec4(v, 0, 1);" +
                        "}",
                "/* Fragment Shader */" +
                        "precision mediump float;" +
                        "varying vec2 tex_coord;" +
                        "uniform float ratio;" +
                        "uniform sampler2D texture;" +
                        "void main() {" +
                        "   float width = ratio < 1.0 ? ratio : 1.0;" +
                        "   float height = ratio >= 1.0 ? 1.0 / ratio : 1.0;" +
                        "   vec2 v = mat2(0.5 * width, 0.0, 0.0, - 0.5 * height) * tex_coord;" +
                        "   v += vec2(0.5, 0.5);" +
                        "   gl_FragColor = texture2D(texture, v);" +
                        "}",
                GL.GL_TRIANGLES, 0, 6
        );
        setAttribute("vertex", false, 0, 0);
        setTexture("texture", texture);

        setBlendEnable(true);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        setUniform("ratio", (float) width / height);
        float h = (float) texture.getHeight() / height;
        float w = (float) texture.getWidth() / width;
        setUniform("h", h);
        setUniform("w", w);
    }
}
