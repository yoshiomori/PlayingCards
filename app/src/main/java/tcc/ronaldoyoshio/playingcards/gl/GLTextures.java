package tcc.ronaldoyoshio.playingcards.gl;

import android.graphics.Bitmap;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Abstração de unidade de textura.
 * Created by mori on 12/07/16.
 */
public class GLTextures {
    int[] textures;
    private int[] params;

    public GLTextures(GLImage[] images, int texturesSize) {
        textures = new int[texturesSize];
        GL.glGenTextures(texturesSize, textures, 0);
        for (GLImage image :
                images) {
            Bitmap data = image.getBitmap();
            if (data != null) {
                GL.glBindTexture(GL.GL_TEXTURE_2D, textures[image.getTextureIndex()]);
                GL.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, data.getWidth(),
                        data.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, adapt(data));
                GL.glTexParameteri(
                        GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                GL.glTexParameteri(
                        GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                GL.glTexParameteri(
                        GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
                GL.glTexParameteri(
                        GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
                GL.glBindTexture(GL.GL_TEXTURE_2D, 0);
            }
        }
        params = new int[2];
    }

    public static Buffer adapt(Bitmap data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[data.getByteCount()]);
        data.copyPixelsToBuffer(byteBuffer);
        byteBuffer.position(0);
        return byteBuffer;
    }

    public void bindTextures(int textureIndex) {
        int texture = textureIndex < 0 ? 0 : this.textures[textureIndex];
        if (getActiveTexture() != GL.GL_TEXTURE0) {
            GL.glActiveTexture(GL.GL_TEXTURE0);
        }
        if (getTexture2DBound() != texture) {
            GL.glBindTexture(GL.GL_TEXTURE_2D, texture);
        }
    }

    private int getActiveTexture() {
        GL.glGetIntegerv(GL.GL_ACTIVE_TEXTURE, params, 0);
        return params[0];
    }

    private int getTexture2DBound() {
        GL.glGetIntegerv(GL.GL_TEXTURE_BINDING_2D, params, 1);
        return params[1];
    }
}
