package tcc.ronaldoyoshio.playingcards.activity.clientConfig;

import android.graphics.Bitmap;

import tcc.ronaldoyoshio.playingcards.gl.GL;
import tcc.ronaldoyoshio.playingcards.gl.GLImage;
import tcc.ronaldoyoshio.playingcards.gl.GLScreen;

/**
 * Responsável por desenhar texturas
 * Created by mori on 10/09/16.
 */
public class TextureImage extends GLImage {
    Bitmap texture;
    private TouchConfigActivity touchConfigActivity;

    public TextureImage(Bitmap texture, TouchConfigActivity touchConfigActivity) {
        this.texture = texture;
        this.touchConfigActivity = touchConfigActivity;
    }

    @Override
    protected void onSurfaceCreated() {
        GLScreen screen = touchConfigActivity.getScreen();
        float h = (float) texture.getHeight() / screen.getHeight() / 2;
        float w = (float) texture.getWidth() / screen.getWidth() / 2;
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
        setUniform("h", h);
        setUniform("w", w);

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
