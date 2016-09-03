package tcc.ronaldoyoshio.playingcards.gl;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Menu principal do app.
 * Created by mori on 06/07/16.
 */
public class GLRenderer implements GLSurfaceView.Renderer{
    private GLImage[] images;
    private int bufferSize = 0;
    private int texturesSize = 0;
    private GLBuffers buffers = null;
    private GLTextures textures;

    public void setImages(GLImage... images) {
        this.images = images;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        for (GLImage image :
                images) {
            image.onSurfaceCreated();
            if (image.getArray() != null) {
                image.setArrayIndex(bufferSize++);
            }
            if (image.getElementArray() != null) {
                image.setElementArrayIndex(bufferSize++);
            }
            if (image.getBitmapId() >= 0) {
                image.setTextureIndex(texturesSize++);
            }
            if (image.getVertexShaderCode() == null | image.getFragmentShaderCode() == null) {
                throw new RuntimeException("Shader deve ser configurado. " +
                        "Utilize o método setShader da class GLImage!");
            }
        }


        GL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        loadDatas();
        loadBuffers();
        loadUnits();
        loadShaderProgram();
    }

    private void loadDatas() {
        for (GLImage image :
                images) {
            image.loadDatas();
        }
    }

    private void loadUnits() {
        textures = new GLTextures(images, texturesSize);
    }

    private void loadBuffers() {
        buffers = new GLBuffers(images, bufferSize);
    }

    private void loadShaderProgram() {
        for (GLImage image:
                images) {
            image.initProgram();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GL.glViewport(0, 0, width, height);
        for (GLImage image :
                images) {
            image.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        for (GLImage image : images) {
            image.render(buffers, textures);
        }
    }
}
