package tcc.ronaldoyoshio.playingcards.activity.main;

import android.content.Intent;
import android.opengl.Matrix;

import tcc.ronaldoyoshio.playingcards.GL.GL;
import tcc.ronaldoyoshio.playingcards.GL.GLImage;
import tcc.ronaldoyoshio.playingcards.R;

/**
 * Dados para criar um botão usando opengl e tratar eventos de toque.
 * Created by mori on 21/07/16.
 */
public class ButtonImage extends GLImage {

    private final MainMenuActivity mainMenuActivity;

    public ButtonImage(MainMenuActivity mainMenuActivity) {
        this.mainMenuActivity = mainMenuActivity;
        setShader(
                "/* Vertex Shader */" +
                        "attribute vec2 vertex;" +
                        "uniform vec2 position;" +
                        "uniform float ratio;" +
                        "varying vec2 tex_coord;" +
                        "mat4 model() {" +
                        "   mat4 m;" +
                        "   m = mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, position, 0, 1);" +
                        "   m *= mat4(0.5, 0, 0, 0, 0, 0.5, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);" +
                        "   m *= mat4(1, 0, 0, 0, 0, 0.216, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);" +
                        "   return m;" +
                        "}" +
                        "mat4 projection() {" +
                        "   mat4 m;" +
                        "   m = mat4(ratio > 1.0 ? 1.0/ratio : 1.0, 0, 0, 0, 0, ratio <= 1.0 ? ratio : 1.0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1);" +
                        "   return m;" +
                        "}" +
                        "void main () {" +
                        "   tex_coord = vertex;" +
                        "   gl_Position = projection() * model() * vec4(vertex, 0, 1);" +
                        "}",
                "/* Fragment Shader */" +
                        "precision mediump float;" +
                        "uniform sampler2D texture;" +
                        "uniform vec2 color;" +
                        "varying vec2 tex_coord;" +
                        "vec2 coord() {" +
                        "   mat3 m;" +
                        "   vec3 v = vec3(tex_coord, 1);" +
                        "   m = mat3(1, 0, 0, 0, 1, 0, color, 1);" +
                        "   m *= mat3(1, 0, 0, 0, 0.5, 0, 0, 0, 1);" +
                        "   m *= mat3(0.5, 0, 0, 0, -0.5, 0, 0.5, 0.5, 1);" +
                        "   v = m * v;" +
                        "   return v.xy;" +
                        "}" +
                        "void main () {" +
                        "   gl_FragColor = texture2D(texture, coord());" +
                        "}",
                GL.GL_TRIANGLES, 6, new short[]{0, 1, 2, 2, 3, 1}
        );
        setAttribute(
                "vertex", true, 0,
                -1f, 1f,
                1f, 1f,
                -1f, -1f,
                1f, -1f
        );

        setRatioName("ratio");

        setTexture("texture", R.drawable.menu_text);

        setPositionName("position");
        setColorName("color");
        GLObject button;

        button = new GLObject();
        button.setPosition(0f, 0.33f);
        button.setColor(0f, 0f);
        add(button);

        button = new GLObject();
        button.setPosition(0f, -0.33f);
        button.setColor(0f, 0.5f);
        add(button);
    }

    @Override
    public void onMove(float dx, float dy) {

    }

    @Override
    public void onDown(float x, float y) {
        System.out.println(x + ", " + y);
        float[] projection = new float[16];
        float[] m = new float[16];
        float[] v = new float[4];
        Matrix.setIdentityM(projection, 0);
        Matrix.scaleM(projection , 0, getRatio() > 1.0f ? 1.0f/getRatio() : 1.0f, getRatio() <= 1.0f ? getRatio() : 1.0f, 1f);
        for (GLObject button :
                this) {
            Matrix.setIdentityM(m, 0);
            float[] position = button.getPosition();
            Matrix.translateM(m, 0, projection, 0, position[0], position[1], 0f);
            Matrix.scaleM(m, 0, 0.5f, 0.5f, 1f);
            Matrix.scaleM(m, 0, 1f, 0.216f, 1f);
            Matrix.invertM(m, 0, m, 0);
            Matrix.multiplyMV(v, 0, m, 0, new float[]{x, y, 0f, 1f}, 0);
            System.out.println(v[0] + ", " + v[1]);
            if (-1 <= v[0] & v[0] <= 1 & -1 <= v[1] & v[1] <= 1) {
                System.out.println("Botão pressionado");
                float[] color = button.getColor();
                if (0f == color[1]) {
                    System.out.println("Hospedar");

                    Intent intent = new Intent(mainMenuActivity, MainActivity.class);
                    mainMenuActivity.startActivity(intent);
                }
                else if (0.5f == color[1]) {
                    System.out.println("Conectar");
                }
            }
        }
    }

    @Override
    public void onUp() {

    }
}
