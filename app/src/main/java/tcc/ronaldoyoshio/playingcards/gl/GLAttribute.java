package tcc.ronaldoyoshio.playingcards.gl;

import java.nio.Buffer;

/**
 * Abstração de attribute.
 * Created by mori on 09/07/16.
 */
public class GLAttribute {
    private int location = -1;
    private int type;
    private boolean normalized = false;
    private int stride = -1;
    private int offset = -1;
    private Buffer array = null;

    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    public void setValue(int offset) {
        this.offset = offset;
    }

    public void setValue(float[] array) {
        this.array = GLBuffers.adapt(array);
    }

    public void define() {
        int size = getSize();

        if (offset >= 0) {
            GL.glVertexAttribPointer(
                    location, size, getType(), normalized, stride * size, offset * size);
        }
        else if (array != null) {
            GL.glVertexAttribPointer(location, size, getType(), normalized, stride * size, array);
        } else {
            throw new RuntimeException("Attribute não inicializado");
        }


        GL.glEnableVertexAttribArray(location);
    }

    private int getType() {
        if (type == GL.GL_FLOAT_VEC4 | type == GL.GL_FLOAT_VEC3 | type == GL.GL_FLOAT_VEC2) {
            return GL.GL_FLOAT;
        }
        else {
            throw new RuntimeException("Tipo do attribute no vertex shader não implementado.");
        }
    }

    private int getSize() {
        if (type == GL.GL_FLOAT_VEC4) {
            return 4;
        }
        else if (type == GL.GL_FLOAT_VEC3) {
            return 3;
        }
        else if (type == GL.GL_FLOAT_VEC2) {
            return 2;
        }
        else {
            throw new RuntimeException("Tipo do attribute no vertex shader não implementado.");
        }
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public void setType(int type) {
        this.type = type;
    }
}
