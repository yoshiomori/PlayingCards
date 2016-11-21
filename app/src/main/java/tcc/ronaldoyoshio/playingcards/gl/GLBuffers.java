package tcc.ronaldoyoshio.playingcards.gl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * Abstração de objeto buffer
 */
public class GLBuffers {
    private int[] buffers;
    private int[] params;

    public GLBuffers(ArrayList<GLImage> arrays, int bufferSize) {
        buffers = new int[bufferSize];
        GL.glGenBuffers(bufferSize, buffers, 0);
        for (GLImage image :
                arrays) {
            if (image.getArray() != null) {
                loadArray(adapt(image.getArray()), image.getArrayIndex());
            }
            if (image.getElementArray() != null) {
                loadArray(adapt(image.getElementArray()), image.getElementArrayIndex());
            }
        }
        params = new int[2];
    }

    private void loadArray(Buffer buffer, int indice) {
        GL.glBindBuffer(GL.GL_ARRAY_BUFFER, buffers[indice]);
        GL.glBufferData(GL.GL_ARRAY_BUFFER, buffer.limit(), buffer, GL.GL_STATIC_DRAW);
    }

    public static ShortBuffer adapt(short[] shortArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(shortArray.length * Float.SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        ShortBuffer floatBuffer = byteBuffer.asShortBuffer();
        floatBuffer.put(shortArray);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static FloatBuffer adapt(float[] floatArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(floatArray.length * Float.SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(floatArray);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public void bindArrayBuffer(int arrayIndex) {
        int buffer = arrayIndex < 0 ? 0 : buffers[arrayIndex];
        if (getArrayBufferBound() != buffer) {
            GL.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer);
        }
    }

    private int getArrayBufferBound() {
        GL.glGetIntegerv(GL.GL_ARRAY_BUFFER_BINDING, params, 0);
        return params[0];
    }

    public void bindElementArrayBuffer(int elementArrayIndex) {
        int buffer = elementArrayIndex < 0 ? 0 : buffers[elementArrayIndex];
        if (getElementArrayBufferBound() != buffer) {
            GL.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer);
        }
    }

    private int getElementArrayBufferBound() {
        GL.glGetIntegerv(GL.GL_ELEMENT_ARRAY_BUFFER_BINDING, params, 1);
        return params[1];
    }
}
