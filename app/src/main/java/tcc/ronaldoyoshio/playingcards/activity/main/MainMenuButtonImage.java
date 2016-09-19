package tcc.ronaldoyoshio.playingcards.activity.main;

import android.content.Intent;
import android.opengl.Matrix;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.TouchEventHandler;
import tcc.ronaldoyoshio.playingcards.activity.config.client.ClientConfigActivity;
import tcc.ronaldoyoshio.playingcards.activity.config.server.ServerConfigActivity;
import tcc.ronaldoyoshio.playingcards.gl.GL;
import tcc.ronaldoyoshio.playingcards.gl.GLImage;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;

/**
 * Dados para criar um botão usando opengl e tratar eventos de toque.
 * Created by mori on 21/07/16.
 */
public class MainMenuButtonImage extends GLImage {
    private float ratio;

    public MainMenuButtonImage(final MainMenuActivity mainMenuActivity) {
        addTouchEventHandler(new TouchEventHandler() {
            @Override
            public boolean onDown(int pointerId, float x, float y) {
                float[] projection = new float[16];
                float[] m = new float[16];
                float[] v = new float[4];
                Matrix.setIdentityM(projection, 0);
                Matrix.scaleM(projection , 0,
                        ratio > 1.0f ? 1.0f/ratio : 1.0f, ratio <= 1.0f ? ratio : 1.0f, 1f);
                for (GLObject button :
                        getObjects()) {
                    Matrix.setIdentityM(m, 0);
                    float[] position = button.getFloats("position");
                    Matrix.translateM(m, 0, projection, 0, position[0], position[1], 0f);
                    Matrix.scaleM(m, 0, 0.5f, 0.5f, 1f);
                    Matrix.scaleM(m, 0, 1f, 0.216f, 1f);
                    Matrix.invertM(m, 0, m, 0);
                    Matrix.multiplyMV(v, 0, m, 0, new float[]{getGLX(x, getWidth()), getGLY(y, getHeight()), 0f, 1f}, 0);
                    if (-1 <= v[0] & v[0] <= 1 & -1 <= v[1] & v[1] <= 1) {
                        float[] color = button.getFloats("color");
                        if (0f == color[1]) {
                            Intent intent = new Intent(mainMenuActivity, ServerConfigActivity.class);
                            mainMenuActivity.startActivity(intent);
                        }
                        else if (0.5f == color[1]) {
                            Intent intent = new Intent(mainMenuActivity, ClientConfigActivity.class);
                            mainMenuActivity.startActivity(intent);
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onSurfaceCreated() {
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

        setTexture("texture", R.drawable.menu_text);

        setObjectUniformNames("position", "color");
        GLObject button;

        ArrayList<GLObject> objects = new ArrayList<>();

        button = new GLObject();
        button.set("position", 0f, 0.33f);
        button.set("color", 0f, 0f);
        objects.add(button);

        button = new GLObject();
        button.set("position", 0f, -0.33f);
        button.set("color", 0f, 0.5f);
        objects.add(button);

        getObjects().addAll(objects);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        ratio = (float) width / height;
        setUniform("ratio", ratio);
    }
}