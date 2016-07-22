package tcc.ronaldoyoshio.playingcards.activity.main;

import tcc.ronaldoyoshio.playingcards.GL.GL;
import tcc.ronaldoyoshio.playingcards.GL.GLImage;
import tcc.ronaldoyoshio.playingcards.R;

public class BackGround extends GLImage {

    public BackGround() {
        setArray(
                -1f, 1f,
                1f, 1f,
                -1f, -1f,
                1f, -1f
        );
        setElementArray(new short[] {0, 1, 2, 1, 2, 3});
        setShader(
                "/* Vertex Shader */" +
                        "attribute vec2 vertex;" +
                        "uniform float ratio;" +
                        "varying vec2 tex_coord;" +
                        "void main() {" +
                        "   float width = ratio < 1.0 ? ratio : 1.0;" +
                        "   float height = ratio >= 1.0 ? 1.0 / ratio : 1.0;" +
                        "   tex_coord = (mat3(0.5 * width, 0.0, 0.0, 0.0, 0.5 * height, 0.0, 0.5, 0.5, 1.0) * vec3(vertex, 1.0)).xy;" +
                        "   gl_Position = vec4(vertex, 0, 1);" +
                        "}",
                "/* Fragment Shader */" +
                        "precision mediump float;" +
                        "varying vec2 tex_coord;" +
                        "uniform sampler2D texture;" +
                        "void main() {" +
                        "  gl_FragColor = texture2D(texture, tex_coord);" +
                        "}",
                GL.GL_TRIANGLES, 0, 6
        );
        setRatioName("ratio");
        setAttribute("vertex", false, 0, 0);
        setTexture("texture", R.drawable.mesa);
    }

    @Override
    public void onMove(float x, float y) {

    }

    @Override
    public void onDown(float x, float y) {

    }

    @Override
    public void onUp() {

    }
}
